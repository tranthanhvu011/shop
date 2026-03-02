package com.accountshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AdminService {
    @Value("${payos.webhook-url}")
    private String webhookUrl;
    @Value("${payos.client-id}")
    private String clientId;
    @Value("${payos.api-key}")
    private String apiKey;
    public String confirmFromConfig() {
        String url = "https://api-merchant.payos.vn/confirm-webhook";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(Map.of("webhookUrl", webhookUrl), headers);
        RestTemplate rt = new RestTemplate();
        return rt.postForEntity(url, entity, String.class).getBody();
    }
}
