package com.accountshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application-level configuration properties.
 * Binds to the 'app' prefix in application.yaml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String uploadDir;
    private String adminEmail;
    private String baseUrl;
    private BankInfo bank;

    @Data
    public static class BankInfo {
        private String name;
        private String accountNumber;
        private String accountHolder;
    }
}
