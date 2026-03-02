package com.accountshop.service;

import com.accountshop.dto.request.PaymentRequest;
import com.accountshop.entity.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    public static final String API_KEY = "b1badf41-73c3-4f04-8b4d-70c5ee0828da";
    public static final String CLIENT_ID = "ff8d8ec5-ccc0-4d72-9c1b-ff0fa5e2d5bb";
    public static final String CHECK_SUM_KEY = "3e6d69ac4db2ced7a7ec9de5bd28b48afc958e7dbe98d537730a6a7e63ed6a7d";
    public PaymentMethod createPaymentLink(Long userId, PaymentRequest paymentRequest) {
        String url = "https://api-merchant.payos.vn/v2/payment-requests";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        headers.set("x-client-id", CLIENT_ID);
        String buildDataToSign = buildDataToSign(paymentRequest.getAmount(), paymentRequest.getCancelUrl(), paymentRequest.getDescription(), paymentRequest.getOrderCode(), paymentRequest.getReturnUrl());
        paymentRequest.setSignature(hmacSha256Hex(buildDataToSign, CHECK_SUM_KEY));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentMethod> response = restTemplate.exchange(url, HttpMethod.POST, request, PaymentMethod.class);
        PaymentMethod body = response.getBody();
        if (body == null) throw new RuntimeException("payOS response null");
        if (body.getData() == null || body.getData().getCheckoutUrl() == null)
            throw new RuntimeException("checkoutUrl null");
        return body;
    }
    // helper
    public static String buildDataToSign(long amount,
                                         String cancelUrl,
                                         String description,
                                         long orderCode,
                                         String returnUrl) {
        // đúng thứ tự alphabet theo docs: amount, cancelUrl, description, orderCode, returnUrl
        return "amount=" + amount
                + "&cancelUrl=" + cancelUrl
                + "&description=" + description
                + "&orderCode=" + orderCode
                + "&returnUrl=" + returnUrl;
    }
    public static String hmacSha256Hex(String data, String checksumKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // to hex lowercase
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign payOS request", e);
        }
    }

 }
