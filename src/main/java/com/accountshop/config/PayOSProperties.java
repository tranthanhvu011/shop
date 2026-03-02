package com.accountshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PayOS payment gateway configuration properties.
 * Binds to the 'payos' prefix in application.yaml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "payos")
public class PayOSProperties {
    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String webhookUrl;
}
