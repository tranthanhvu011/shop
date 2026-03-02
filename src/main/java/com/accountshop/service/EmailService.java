package com.accountshop.service;

import com.accountshop.dto.EmailEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Kafka Producer — sends email events to email-topic.
 * Services inject this to send async emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "email-topic";

    public void sendOtp(String email, String otp) {
        send(EmailEvent.builder()
                .eventType("OTP_EMAIL_REGISTERED")
                .to(email)
                .templateData(otp)
                .build());
    }

    public void sendWelcome(String email, String username) {
        send(EmailEvent.builder()
                .eventType("WELCOME_EMAIL")
                .to(email)
                .templateData(username)
                .build());
    }

    public void sendResetPassword(String email, String resetLink) {
        send(EmailEvent.builder()
                .eventType("RESET_PASSWORD_EMAIL")
                .to(email)
                .templateData(resetLink)
                .build());
    }

    public void sendOrderEmail(String email, String subject, String htmlContent) {
        send(EmailEvent.builder()
                .eventType("EMAIL_SEND")
                .to(email)
                .subject(subject)
                .htmlContent(htmlContent)
                .build());
    }

    private void send(EmailEvent event) {
        try {
            event.setEventId(UUID.randomUUID().toString());
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getEventId(), json);
            log.info("📧 Kafka email event sent: type={}, to={}", event.getEventType(), event.getTo());
        } catch (Exception e) {
            log.error("❌ Failed to send Kafka email event: {}", e.getMessage(), e);
        }
    }
}
