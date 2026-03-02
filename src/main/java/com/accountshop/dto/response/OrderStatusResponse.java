package com.accountshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for order status polling.
 * Used by GET /api/orders/{orderNumber}/status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
    private String paymentStatus;
    private String orderNumber;
}
