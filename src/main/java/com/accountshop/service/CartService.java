package com.accountshop.service;

import com.accountshop.dto.CartItem;
import com.accountshop.entity.Product;
import com.accountshop.entity.ProductVariant;
import com.accountshop.entity.VariantPricing;
import com.accountshop.repository.ProductRepository;
import com.accountshop.repository.VariantPricingRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CartService — Redis-based shopping cart.
 * Ported from Cart-Service microservice, uses direct repo calls instead of HTTP.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final VariantPricingRepository pricingRepository;
    private final ProductRepository productRepository;

    private static final String CART_PREFIX = "cart:";
    private static final int CART_EXPIRE_DAYS = 30;
    private static final int CART_MAX_ITEMS = 10000;
    private static final int CART_MAX_QUANTITY = 1000;

    /**
     * Get full cart with enriched product info.
     */
    public Map<String, Object> getCart(Long userId) {
        String cartKey = getCartKey(userId);
        Map<Object, Object> entries;
        try {
            entries = redisTemplate.opsForHash().entries(cartKey);
        } catch (Exception e) {
            log.warn("Failed to deserialize cart for userId={}, clearing corrupted data: {}", userId, e.getMessage());
            redisTemplate.delete(cartKey);
            return Map.of("items", Collections.emptyList(), "totalItems", 0,
                    "totalQuantity", 0, "totalPrice", BigDecimal.ZERO);
        }

        if (entries.isEmpty()) {
            return Map.of("items", Collections.emptyList(), "totalItems", 0,
                    "totalQuantity", 0, "totalPrice", BigDecimal.ZERO);
        }

        List<CartItem> items = entries.values().stream()
                .map(this::convertToCartItem)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CartItem::getAddedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // Enrich with product info from DB
        for (CartItem item : items) {
            enrichCartItem(item);
        }

        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal totalPrice = items.stream()
                .filter(i -> i.getSubtotal() != null)
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("items", items, "totalItems", items.size(),
                "totalQuantity", totalQuantity, "totalPrice", totalPrice);
    }

    /**
     * Add item to cart.
     */
    public CartItem addItem(Long userId, Long productId, Long variantId, Long pricingId, int quantity) {
        String cartKey = getCartKey(userId);
        String field = String.valueOf(pricingId);

        // Check max items
        Long currentSize = redisTemplate.opsForHash().size(cartKey);
        if (currentSize != null && currentSize >= CART_MAX_ITEMS) {
            if (!Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(cartKey, field))) {
                throw new RuntimeException("Giỏ hàng đã đạt giới hạn: " + CART_MAX_ITEMS + "sản phẩm");
            }
        }

        // Merge quantity if existing
        Object existing = redisTemplate.opsForHash().get(cartKey, field);
        int newQuantity = quantity;
        LocalDateTime addedAt = LocalDateTime.now();
        if (existing != null) {
            CartItem cartItem = convertToCartItem(existing);
            if (cartItem != null) {
                newQuantity = Math.min(cartItem.getQuantity() + quantity, CART_MAX_QUANTITY);
                addedAt = cartItem.getAddedAt();
            }
        }
        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .variantId(variantId)
                .pricingId(pricingId)
                .quantity(newQuantity)
                .addedAt(addedAt).build();
        redisTemplate.opsForHash().put(cartKey, field, cartItem);
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);

        enrichCartItem(cartItem);
        log.info("Added item to cart: userId={}, pricingId={}, quantity={}", userId, pricingId, newQuantity);
        return cartItem;
    }

    /**
     * Update item quantity.
     */
    public CartItem updateQuantity(Long userId, Long pricingId, int quantity) {
        String cartKey = getCartKey(userId);
        String field = String.valueOf(pricingId);

        Object existing = redisTemplate.opsForHash().get(cartKey, field);
        if (existing == null) {
            throw new RuntimeException("Sản phẩm không có trong giỏ hàng");
        }

        CartItem cartItem = convertToCartItem(existing);
        if (cartItem == null) {
            throw new RuntimeException("Dữ liệu giỏ hàng lỗi");
        }

        cartItem.setQuantity(Math.min(quantity, CART_MAX_QUANTITY));
        redisTemplate.opsForHash().put(cartKey, field, cartItem);

        enrichCartItem(cartItem);
        log.info("Updated cart item: userId={}, pricingId={}, newQuantity={}", userId, pricingId, quantity);
        return cartItem;
    }

    /**
     * Remove single item.
     */
    public void removeItem(Long userId, Long pricingId) {
       String cartKey = getCartKey(userId);
       String field = String.valueOf(pricingId);
       redisTemplate.opsForHash().delete(cartKey, field);
    }

    /**
     * Clear entire cart.
     */
    public void clearCart(Long userId) {
        redisTemplate.delete(getCartKey(userId));
        log.info("Cleared cart: userId={}", userId);
    }

    /**
     * Count items in cart.
     */
    public Long getCartCount(Long userId) {
        return redisTemplate.opsForHash().size(getCartKey(userId));
    }

    // =========================================
    // Helpers
    // =========================================

    private void enrichCartItem(CartItem item) {
        try {
            pricingRepository.findById(item.getPricingId()).ifPresent(pricing -> {
                item.setUnitPrice(pricing.getPrice());
                item.setDurationLabel(pricing.getDurationLabel());
                item.setSubtotal(pricing.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                if (pricing.getVariant() != null) {
                    ProductVariant variant = pricing.getVariant();
                    item.setVariantName(variant.getName());
                    if (variant.getProduct() != null) {
                        Product product = variant.getProduct();
                        item.setProductName(product.getName());
                        item.setProductSlug(product.getSlug());
                        if (product.getImages() != null && !product.getImages().isEmpty()) {
                            item.setImageUrl(product.getImages().get(0).getImageUrl());
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.warn("Failed to enrich cart item pricingId={}: {}", item.getPricingId(), e.getMessage());
        }
    }

    private String getCartKey(Long userId) {
        return CART_PREFIX + userId;
    }

    @SuppressWarnings("unchecked")
    private CartItem convertToCartItem(Object obj) {
        if (obj instanceof CartItem) return (CartItem) obj;
        if (obj instanceof Map) {
            try {
                Map<String, Object> map = (Map<String, Object>) obj;
                return CartItem.builder()
                        .productId(toLong(map.get("productId")))
                        .variantId(toLong(map.get("variantId")))
                        .pricingId(toLong(map.get("pricingId")))
                        .quantity(toInt(map.get("quantity")))
                        .addedAt(map.get("addedAt") != null ? LocalDateTime.parse(map.get("addedAt").toString()) : null)
                        .build();
            } catch (Exception e) {
                log.warn("Failed to convert cart item from map: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    private Long toLong(Object obj) {
        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof String) return Long.parseLong((String) obj);
        return null;
    }

    private Integer toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return null;
    }
}
