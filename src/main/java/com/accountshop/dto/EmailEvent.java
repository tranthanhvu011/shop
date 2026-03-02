package com.accountshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    private String eventId;
    private String to;
    private String subject;
    private String eventType;    // OTP_EMAIL_REGISTERED, WELCOME_EMAIL, RESET_PASSWORD_EMAIL, EMAIL_SEND
    private Object templateData; // OTP code, username, reset token, etc.
    private String htmlContent;  // For rich HTML emails (order confirmation)
}
