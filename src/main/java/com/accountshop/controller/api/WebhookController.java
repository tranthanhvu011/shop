package com.accountshop.controller.api;

import com.accountshop.entity.Order;
import com.accountshop.repository.OrderRepository;
import com.accountshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WebhookController — handles external payment webhooks.
 * Routes: POST /webhook/payos
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @SuppressWarnings("unchecked")
    @PostMapping("/webhook/payos")
    public ResponseEntity<?> handlePayOSWebhook(@RequestBody Map<String, Object> body) {
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        String receivedSignature = (String) body.get("signature");

        if (!paymentService.verifyWebhookSignature(data, receivedSignature)) {
            log.warn("[Webhook] Invalid PayOS signature");
            return ResponseEntity.status(403).body("Invalid signature");
        }

        String code = (String) data.get("code");
        Long orderCode = Long.valueOf(data.get("orderCode").toString());

        if ("00".equals(code)) {
            Order order = orderRepository.findById(orderCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
            log.info("[Webhook] PayOS payment confirmed for orderId={}", orderCode);
        }
        return ResponseEntity.ok().build();
    }
}
