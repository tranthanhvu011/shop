package com.accountshop.service;

import com.accountshop.dto.EmailEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer — listens for email events and dispatches to EmailSenderService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailSenderService emailSenderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "email-topic", groupId = "accountshop-email-group")
    public void handleEmailEvent(String message) {
        log.info("📩 Received Kafka email event");

        try {
            EmailEvent event = objectMapper.readValue(message, EmailEvent.class);
            log.info("Parsed email event: {} for {}", event.getEventType(), event.getTo());

            switch (event.getEventType()) {
                case "OTP_EMAIL_REGISTERED" -> {
                    String otp = (String) event.getTemplateData();
                    emailSenderService.sendOtpEmail(event.getTo(), otp);
                }
                case "WELCOME_EMAIL" -> {
                    String username = (String) event.getTemplateData();
                    emailSenderService.sendWelcomeEmail(event.getTo(), username);
                }
                case "RESET_PASSWORD_EMAIL" -> {
                    String token = (String) event.getTemplateData();
                    emailSenderService.sendResetPasswordEmail(event.getTo(), token);
                }
                case "EMAIL_SEND" -> {
                    if (event.getHtmlContent() != null) {
                        emailSenderService.sendHtmlEmail(event.getTo(), event.getSubject(), event.getHtmlContent());
                    } else {
                        emailSenderService.sendSimpleEmail(event.getTo(), event.getSubject(),
                                event.getTemplateData() != null ? event.getTemplateData().toString() : "");
                    }
                }
                default -> log.warn("Unknown email event type: {}", event.getEventType());
            }

            log.info("✅ Email sent successfully for event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("❌ Failed to process email event: {}", e.getMessage(), e);
        }
    }
}
