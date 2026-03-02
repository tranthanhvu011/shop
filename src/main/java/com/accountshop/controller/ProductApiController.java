package com.accountshop.controller;

import com.accountshop.entity.Product;
import com.accountshop.entity.ProductVariant;
import com.accountshop.entity.VariantPricing;
import com.accountshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductApiController — REST API for product data.
 * Used by the preview modal on the explore page.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    /**
     * Returns variant/pricing data for preview modal.
     */
    @GetMapping("/{id}/variants")
    public ResponseEntity<?> getProductVariants(@PathVariable Long id) {
        Product product = productService.findById(id)
                .orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> variants = new ArrayList<>();
        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                Map<String, Object> vm = new LinkedHashMap<>();
                vm.put("id", v.getId());
                vm.put("name", v.getName());

                List<Map<String, Object>> pricings = new ArrayList<>();
                if (v.getPricingList() != null) {
                    for (VariantPricing p : v.getPricingList()) {
                        Map<String, Object> pm = new LinkedHashMap<>();
                        pm.put("id", p.getId());
                        pm.put("durationLabel", p.getDurationLabel());
                        pm.put("price", p.getPrice());
                        pm.put("originalPrice", p.getOriginalPrice());
                        pm.put("stock", p.getAvailableStock() != null ? p.getAvailableStock() : 0);
                        pricings.add(pm);
                    }
                }
                vm.put("pricings", pricings);
                variants.add(vm);
            }
        }
        return ResponseEntity.ok(Map.of(
                "id", product.getId(),
                "name", product.getName(),
                "slug", product.getSlug(),
                "variants", variants
        ));
    }
}
