package com.accountshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AdminService — admin-level operations (webhook config, etc.)
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PayOSProperties payOSConfig;

    /**
     * Confirm PayOS webhook URL with PayOS API.
     */
    public String confirmFromConfig() {
        String url = "https://api-merchant.payos.vn/confirm-webhook";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", payOSConfig.getClientId());
        headers.set("x-api-key", payOSConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(Map.of("webhookUrl", payOSConfig.getWebhookUrl()), headers);
        RestTemplate rt = new RestTemplate();
        return rt.postForEntity(url, entity, String.class).getBody();
    }
}
