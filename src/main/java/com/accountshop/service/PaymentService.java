package com.accountshop.service;

import com.accountshop.config.PayOSProperties;
import com.accountshop.dto.request.PaymentRequest;
import com.accountshop.entity.Order;
import com.accountshop.entity.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * PaymentService — centralized PayOS payment gateway integration.
 * All PayOS API calls and signature verification go through here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String PAYOS_API_URL = "https://api-merchant.payos.vn/v2/payment-requests";

    private final PayOSProperties payOSConfig;

    /**
     * Create a PayOS payment link for the given order.
     *
     * @param order     the order to create payment for
     * @param cancelUrl the URL to redirect to on cancellation
     * @param returnUrl the URL to redirect to on success
     * @return PaymentMethod response containing checkout URL and QR code
     */
    public PaymentMethod createPaymentLink(Order order, String cancelUrl, String returnUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", payOSConfig.getApiKey());
        headers.set("x-client-id", payOSConfig.getClientId());
        headers.setContentType(MediaType.APPLICATION_JSON);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderCode(Math.toIntExact(order.getId()));

        BigDecimal totalAmount = order.getTotalAmount();
        long amountLong = totalAmount.longValueExact();
        if (amountLong <= 0) {
            throw new RuntimeException("Số tiền thanh toán không hợp lệ");
        }
        if (amountLong > Integer.MAX_VALUE) {
            throw new RuntimeException("Số tiền vượt quá giới hạn PayOS (~2.1 tỷ VND)");
        }
        paymentRequest.setAmount((int) amountLong);
        paymentRequest.setDescription("DH " + order.getOrderNumber());
        paymentRequest.setCancelUrl(cancelUrl);
        paymentRequest.setReturnUrl(returnUrl);

        // Sign the request
        String dataToSign = buildOrderDataToSign(
                paymentRequest.getAmount(),
                paymentRequest.getCancelUrl(),
                paymentRequest.getDescription(),
                paymentRequest.getOrderCode(),
                paymentRequest.getReturnUrl()
        );
        paymentRequest.setSignature(hmacSha256Hex(dataToSign, payOSConfig.getChecksumKey()));

        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentMethod> response = restTemplate.exchange(
                PAYOS_API_URL, HttpMethod.POST, request, PaymentMethod.class
        );

        PaymentMethod body = response.getBody();
        log.info("[PayOS] Response for order #{}: code={}, desc={}",
                order.getOrderNumber(),
                body != null ? body.getCode() : "null",
                body != null ? body.getDesc() : "null");

        if (body == null) {
            throw new RuntimeException("PayOS response null");
        }
        if (body.getData() == null || body.getData().getCheckoutUrl() == null) {
            throw new RuntimeException("PayOS checkoutUrl null, code=" + body.getCode() + ", desc=" + body.getDesc());
        }
        return body;
    }

    /**
     * Verify PayOS webhook signature.
     *
     * @param data              the webhook data map
     * @param receivedSignature the signature sent by PayOS
     * @return true if signature is valid
     */
    public boolean verifyWebhookSignature(Map<String, Object> data, String receivedSignature) {
        String dataToSign = "accountNumber=" + data.get("accountNumber")
                + "&amount=" + data.get("amount")
                + "&code=" + data.get("code")
                + "&counterAccountBankId=" + data.get("counterAccountBankId")
                + "&counterAccountBankName=" + data.get("counterAccountBankName")
                + "&counterAccountName=" + data.get("counterAccountName")
                + "&counterAccountNumber=" + data.get("counterAccountNumber")
                + "&currency=" + data.get("currency")
                + "&desc=" + data.get("desc")
                + "&description=" + data.get("description")
                + "&orderCode=" + data.get("orderCode")
                + "&paymentLinkId=" + data.get("paymentLinkId")
                + "&reference=" + data.get("reference")
                + "&transactionDateTime=" + data.get("transactionDateTime")
                + "&virtualAccountName=" + data.get("virtualAccountName")
                + "&virtualAccountNumber=" + data.get("virtualAccountNumber");

        String expectedSignature = hmacSha256Hex(dataToSign, payOSConfig.getChecksumKey());
        return expectedSignature.equals(receivedSignature);
    }

    // ── Private helpers ──

    private static String buildOrderDataToSign(long amount, String cancelUrl,
                                                String description, long orderCode, String returnUrl) {
        // Alphabetical order as per PayOS docs
        return "amount=" + amount
                + "&cancelUrl=" + cancelUrl
                + "&description=" + description
                + "&orderCode=" + orderCode
                + "&returnUrl=" + returnUrl;
    }

    private static String hmacSha256Hex(String data, String checksumKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            );
            mac.init(keySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign PayOS request", e);
        }
    }
}
