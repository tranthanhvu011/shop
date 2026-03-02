package com.accountshop.controller;

import com.accountshop.entity.User;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CartApiController — REST API for AJAX cart operations.
 * Used by product-detail.html and cart.html for add/update/remove.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<?> getCart() {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody Map<String, Object> request) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

        Long productId = Long.parseLong(request.get("productId").toString());
        Long variantId = request.get("variantId") != null ? Long.parseLong(request.get("variantId").toString()) : null;
        Long pricingId = Long.parseLong(request.get("pricingId").toString());
        int quantity = request.get("quantity") != null ? Integer.parseInt(request.get("quantity").toString()) : 1;

        var item = cartService.addItem(user.getId(), productId, variantId, pricingId, quantity);
        return ResponseEntity.ok(Map.of("success", true, "item", item,
                "cartCount", cartService.getCartCount(user.getId())));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Object> request) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

        Long pricingId = Long.parseLong(request.get("pricingId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());

        var item = cartService.updateQuantity(user.getId(), pricingId, quantity);
        return ResponseEntity.ok(Map.of("success", true, "item", item));
    }

    @DeleteMapping("/remove/{pricingId}")
    public ResponseEntity<?> removeItem(@PathVariable Long pricingId) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        cartService.removeItem(user.getId(), pricingId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCartCount() {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        return ResponseEntity.ok(Map.of("count", cartService.getCartCount(user.getId())));
    }
}
