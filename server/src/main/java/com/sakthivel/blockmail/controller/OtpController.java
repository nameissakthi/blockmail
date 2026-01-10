package com.sakthivel.blockmail.controller;

import com.sakthivel.blockmail.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * OTP Controller for Email Verification
 */
@RestController
@RequestMapping("/api/otp")
@Slf4j
public class OtpController {

    private final OtpService otpService;

    public OtpController(@Autowired OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Generate and send OTP
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Email is required"
                ));
            }

            otpService.generateAndSendOtp(email);

            log.info("OTP generated for email: {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP sent successfully to your email. Please check your inbox."
            ));

        } catch (Exception e) {
            log.error("Failed to generate OTP", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Verify OTP
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otpCode = request.get("otpCode");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Email is required"
                ));
            }

            if (otpCode == null || otpCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "OTP code is required"
                ));
            }

            boolean verified = otpService.verifyOtp(email, otpCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "verified", verified,
                    "message", "Email verified successfully!"
            ));

        } catch (Exception e) {
            log.error("Failed to verify OTP", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
