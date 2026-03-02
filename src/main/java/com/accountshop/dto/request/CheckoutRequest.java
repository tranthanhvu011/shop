package com.accountshop.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Request DTO for checkout form submission.
 * Replaces 5 individual @RequestParam in CheckoutController.processCheckout().
 */
@Data
public class CheckoutRequest {
    private String paymentMethod;
    private String couponCode;
    private String note;
    private List<Long> pricingIds;
    private List<Integer> quantities;
}
