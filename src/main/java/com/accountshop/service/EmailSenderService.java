package com.accountshop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Sender Service — handles actual SMTP delivery.
 * Ported 1:1 from email-service/EmailSenderService.java
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String body) {
        log.info("Sending simple email to: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email sent successfully to: {}", to);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        log.info("Sending HTML email to: {}", to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("HTML email sent successfully to: {}", to);
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Mã xác thực OTP - AccountShop";
        String body = String.format("""
            Xin chào,

            Mã OTP của bạn là: %s

            Mã này sẽ hết hạn sau 5 phút.
            Vui lòng không chia sẻ mã này với bất kỳ ai.

            Trân trọng,
            AccountShop Team
            """, otp);
        sendSimpleEmail(to, subject, body);
    }

    public void sendWelcomeEmail(String to, String username) {
        String subject = "Chào mừng bạn đến với AccountShop!";
        String body = String.format("""
            Xin chào %s,

            Chúc mừng bạn đã đăng ký tài khoản thành công!
            Bạn có thể đăng nhập và bắt đầu mua sắm.

            Trân trọng,
            AccountShop Team
            """, username);
        sendSimpleEmail(to, subject, body);
    }

    public void sendResetPasswordEmail(String to, String resetLink) {
        String subject = "Yêu cầu đặt lại mật khẩu";
        String body = String.format("""
            Xin chào,

            Bạn đã yêu cầu đặt lại mật khẩu.
            Đường dẫn: %s

            Đường dẫn này sẽ hết hạn sau 15 phút.
            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.

            Trân trọng,
            AccountShop Team
            """, resetLink);
        sendSimpleEmail(to, subject, body);
    }
}
