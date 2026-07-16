package com.vulnweb.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vulnweb.auth.config.AppProperties;
import com.vulnweb.auth.entity.User;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public EmailService(JavaMailSender mailSender, AppProperties appProperties) {
        this.mailSender = mailSender;
        this.appProperties = appProperties;
    }

    @Async
    public void sendVerificationEmail(User user, String token) {
        String verificationUrl = appProperties.getFrontend().getBaseUrl()
                + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your email address");
        message.setText("Hello " + user.getFirstName() + ",\n\n"
                + "Please verify your email by clicking the link below:\n"
                + verificationUrl + "\n\n"
                + "This link expires in " + appProperties.getAuth().getVerificationTokenExpiryHours() + " hours.\n\n"
                + "If you did not create an account, you can ignore this email.");

        send(message);
    }

    @Async
    public void sendPasswordResetEmail(User user, String token) {
        String resetUrl = appProperties.getFrontend().getBaseUrl()
                + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText("Hello " + user.getFirstName() + ",\n\n"
                + "You requested a password reset. Click the link below to set a new password:\n"
                + resetUrl + "\n\n"
                + "This link expires in " + appProperties.getAuth().getPasswordResetTokenExpiryHours() + " hour(s).\n\n"
                + "If you did not request this, you can safely ignore this email.");

        send(message);
    }

    private void send(SimpleMailMessage message) {
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", message.getTo(), ex.getMessage());
        }
    }
}
