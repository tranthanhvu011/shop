package com.accountshop.controller.user;

import com.accountshop.common.ApiResponse;
import com.accountshop.config.AppProperties;
import com.accountshop.dto.CartItem;
import com.accountshop.dto.response.OrderStatusResponse;
import com.accountshop.entity.*;
import com.accountshop.exception.BusinessException;
import com.accountshop.exception.ResourceNotFoundException;
import com.accountshop.exception.UnauthorizedException;
import com.accountshop.repository.OrderRepository;
import com.accountshop.repository.VariantPricingRepository;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.CartService;
import com.accountshop.service.OrderService;
import com.accountshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CheckoutController — handles checkout page and payment processing.
 * Routes: GET/POST /checkout, GET /api/orders/{orderNumber}/status
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final SecurityUtils securityUtils;
    private final OrderRepository orderRepository;
    private final VariantPricingRepository variantPricingRepository;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AppProperties appProperties;

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(required = false) List<Long> pricingIds,
            @RequestParam(required = false) List<Integer> quantities,
            Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(UnauthorizedException::new);
        model.addAttribute("currentUser", user);
        List<CartItem> cartItems;
        if (pricingIds != null && !pricingIds.isEmpty()) {
            cartItems = new ArrayList<>();
            for (int i = 0; i < pricingIds.size(); i++) {
                int qty = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1;
                variantPricingRepository.findById(pricingIds.get(i)).ifPresent(pricing -> {
                    CartItem item = new CartItem();
                    item.setPricingId(pricing.getId());
                    item.setUnitPrice(pricing.getPrice());
                    item.setOriginalPrice(pricing.getOriginalPrice());
                    item.setDurationLabel(pricing.getDurationLabel());
                    item.setQuantity(qty);
                    item.setSubtotal(pricing.getPrice().multiply(BigDecimal.valueOf(qty)));
                    if (pricing.getVariant() != null) {
                        item.setVariantId(pricing.getVariant().getId());
                        item.setVariantName(pricing.getVariant().getName());
                        if (pricing.getVariant().getProduct() != null) {
                            Product product = pricing.getVariant().getProduct();
                            item.setProductId(product.getId());
                            item.setProductName(product.getName());
                            item.setProductSlug(product.getSlug());
                            if (product.getImages() != null && !product.getImages().isEmpty()) {
                                item.setImageUrl(product.getImages().get(0).getImageUrl());
                            }
                        }
                    }
                    cartItems.add(item);
                });
            }
        } else {
            @SuppressWarnings("unchecked")
            List<CartItem> allItems = (List<CartItem>) cartService.getCart(user.getId()).getOrDefault("items", Collections.emptyList());
            cartItems = allItems;
        }

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("pricingIds", cartItems.stream().map(CartItem::getPricingId).collect(Collectors.toList()));
        model.addAttribute("quantities", cartItems.stream().map(CartItem::getQuantity).collect(Collectors.toList()));
        BigDecimal subtotal = cartItems.stream()
                .filter(i -> i.getUnitPrice() != null)
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("subtotal", subtotal);

        // Bank info from config
        if (appProperties.getBank() != null) {
            model.addAttribute("nameBank", appProperties.getBank().getName());
            model.addAttribute("numberBank", appProperties.getBank().getAccountNumber());
            model.addAttribute("nameUserBank", appProperties.getBank().getAccountHolder());
        }
        return "user/checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<PaymentMethod> processCheckout(
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) List<Long> pricingIds,
            @RequestParam(required = false) List<Integer> quantities,
            RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(UnauthorizedException::new);
        if (pricingIds == null || pricingIds.isEmpty()) {
            throw new BusinessException("Không có sản phẩm nào");
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < pricingIds.size(); i++) {
            final int idx = i;
            VariantPricing variantPricing = variantPricingRepository.findById(pricingIds.get(i))
                    .orElseThrow(() -> new ResourceNotFoundException("VariantPricing", pricingIds.get(idx)));
            ProductVariant variant = variantPricing.getVariant();
            Product product = variant.getProduct();
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("variantId", variant.getId());
            item.put("pricingId", variantPricing.getId());
            item.put("unitPrice", variantPricing.getPrice());
            item.put("quantity", quantities.get(i));
            item.put("productName", product.getName());
            item.put("productSlug", product.getSlug());
            item.put("variantName", variant.getName());
            item.put("durationLabel", variantPricing.getDurationLabel());
            item.put("productImage", product.getImages().isEmpty() ? "" : product.getImages().get(0).getImageUrl());
            items.add(item);
        }
        Order order = orderService.createOrder(user, paymentMethod, couponCode, note, items);

        if ("BANK_TRANSFER".equals(paymentMethod)) {
            String baseUrl = appProperties.getBaseUrl();
            PaymentMethod result = paymentService.createPaymentLink(
                    order,
                    baseUrl + "/orders/cancel",
                    baseUrl + "/orders/" + order.getOrderNumber()
            );
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/orders/{orderNumber}/status")
    @ResponseBody
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getOrderStatus(@PathVariable String orderNumber) {
        Order order = orderRepository.findById(Long.valueOf(orderNumber))
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderNumber));
        OrderStatusResponse dto = OrderStatusResponse.builder()
                .paymentStatus(order.getPaymentStatus().name())
                .orderNumber(order.getOrderNumber())
                .build();
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
