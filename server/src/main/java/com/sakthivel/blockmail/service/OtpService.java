package com.sakthivel.blockmail.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * OTP Service for Email Verification (In-Memory Storage)
 */
@Service
@Slf4j
public class OtpService {

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // In-memory storage for OTPs (thread-safe)
    private final ConcurrentHashMap<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    public OtpService(@Autowired JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Inner class to store OTP data
     */
    @Data
    @AllArgsConstructor
    private static class OtpData {
        private String otpCode;
        private LocalDateTime expiresAt;
        private int attempts;
    }

    /**
     * Generate and send OTP to email
     */
    public void generateAndSendOtp(String email) {
        // Validate email format
        if (!isValidEmail(email)) {
            throw new RuntimeException("Invalid email format");
        }

        // Remove any existing OTP for this email
        otpStore.remove(email);

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));

        // Store OTP in memory with expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStore.put(email, new OtpData(otpCode, expiresAt, 0));
        
        log.info("Generated OTP for email: {} (valid for {} minutes)", email, OTP_EXPIRY_MINUTES);

        // Send OTP via email
        sendOtpEmail(email, otpCode);
    }

    /**
     * Verify OTP
     */
    public boolean verifyOtp(String email, String otpCode) {
        OtpData otpData = otpStore.get(email);

        if (otpData == null) {
            throw new RuntimeException("No OTP found for this email. Please request a new OTP.");
        }

        // Check if expired
        if (LocalDateTime.now().isAfter(otpData.getExpiresAt())) {
            otpStore.remove(email); // Clean up expired OTP
            throw new RuntimeException("OTP has expired. Please request a new OTP.");
        }

        // Check max attempts
        if (otpData.getAttempts() >= MAX_ATTEMPTS) {
            otpStore.remove(email); // Clean up after max attempts
            throw new RuntimeException("Maximum verification attempts exceeded. Please request a new OTP.");
        }

        // Increment attempts
        otpData.setAttempts(otpData.getAttempts() + 1);

        // Verify OTP
        if (!otpData.getOtpCode().equals(otpCode)) {
            int remainingAttempts = MAX_ATTEMPTS - otpData.getAttempts();
            throw new RuntimeException("Invalid OTP. " + remainingAttempts + " attempts remaining.");
        }

        // OTP verified successfully - remove it immediately
        otpStore.remove(email);
        log.info("OTP verified successfully for email: {} (removed from memory)", email);
        return true;
    }

    /**
     * Send OTP email
     */
    private void sendOtpEmail(String email, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("BlockMail - Email Verification OTP");

            String emailBody = String.format("""
                    <html>
                    <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                        <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 10px; text-align: center;">
                            <h1 style="color: white; margin: 0;">🔐 BlockMail</h1>
                            <p style="color: white; margin: 10px 0;">Quantum Secure Email Client</p>
                        </div>
                        
                        <div style="padding: 30px; background: #f9f9f9; border-radius: 10px; margin-top: 20px;">
                            <h2 style="color: #333;">Email Verification</h2>
                            <p style="color: #666; line-height: 1.6;">
                                Thank you for registering with BlockMail! Please use the following OTP to verify your email address:
                            </p>
                            
                            <div style="background: white; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0;">
                                <h1 style="color: #667eea; font-size: 48px; letter-spacing: 8px; margin: 0;">%s</h1>
                            </div>
                            
                            <p style="color: #999; font-size: 12px;">
                                This OTP will expire in %d minutes. Do not share this code with anyone.
                            </p>
                            
                            <p style="color: #666; margin-top: 20px;">
                                If you didn't request this OTP, please ignore this email.
                            </p>
                        </div>
                        
                        <div style="text-align: center; margin-top: 20px; color: #999; font-size: 12px;">
                            <p>BlockMail - Quantum Secure Communication Platform</p>
                        </div>
                    </body>
                    </html>
                    """, otpCode, OTP_EXPIRY_MINUTES);

            helper.setText(emailBody, true);
            mailSender.send(message);

            log.info("OTP email sent successfully to: {}", email);

        } catch (jakarta.mail.MessagingException e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send OTP email. Please check your email address and try again.");
        }
    }

    /**
     * Validate email format and check if it's a real email provider
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email format validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            return false;
        }

        // Check for common fake email patterns
        String lowerEmail = email.toLowerCase();
        String[] fakePatterns = {"test@", "fake@", "dummy@", "temp@", "noreply@"};
        for (String pattern : fakePatterns) {
            if (lowerEmail.startsWith(pattern)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Cleanup expired OTPs from memory (runs every 5 minutes)
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        int beforeSize = otpStore.size();
        
        // Remove all expired OTPs
        otpStore.entrySet().removeIf(entry -> 
            now.isAfter(entry.getValue().getExpiresAt())
        );
        
        int removedCount = beforeSize - otpStore.size();
        if (removedCount > 0) {
            log.info("Cleaned up {} expired OTPs from memory", removedCount);
        }
    }

    /**
     * Get current OTP store size (for monitoring)
     */
    public int getOtpStoreSize() {
        return otpStore.size();
    }
}