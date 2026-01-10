package com.sakthivel.blockmail.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakthivel.blockmail.dto.EmailResponseDTO;
import com.sakthivel.blockmail.dto.SendEmailRequestDTO;
import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.service.SecureEmailService;
import com.sakthivel.blockmail.service.UserService;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/quantum-email")
@Slf4j
public class QuantumEmailController {
    private final SecureEmailService secureEmailService;
    private final UserService userService;
    public QuantumEmailController(
            @Autowired SecureEmailService secureEmailService,
            @Autowired UserService userService) {
        this.secureEmailService = secureEmailService;
        this.userService = userService;
    }
    @PostMapping("/send")
    public ResponseEntity<?> sendSecureEmail(@RequestBody SendEmailRequestDTO request) {
        try {
            User currentUser = getCurrentUser();
            EmailResponseDTO response = secureEmailService.sendSecureEmail(request, currentUser);
            log.info("Secure email sent successfully by user: {}", currentUser.getEmail());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email sent successfully with " + request.getSecurityLevel().getName(),
                    "email", response
            ));
        } catch (Exception e) {
            log.error("Failed to send secure email", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to send email: " + e.getMessage()
            ));
        }
    }
    @GetMapping("/sent")
    public ResponseEntity<?> getSentEmails() {
        try {
            User currentUser = getCurrentUser();
            List<EmailResponseDTO> emails = secureEmailService.getSentEmails(currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", emails.size(),
                    "emails", emails
            ));
        } catch (Exception e) {
            log.error("Failed to retrieve sent emails", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedEmails() {
        try {
            User currentUser = getCurrentUser();
            List<EmailResponseDTO> emails = secureEmailService.getReceivedEmails(currentUser.getEmail());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", emails.size(),
                    "emails", emails
            ));
        } catch (Exception e) {
            log.error("Failed to retrieve received emails", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/decrypt/{emailId}")
    public ResponseEntity<?> decryptEmail(@PathVariable String emailId) {
        try {
            User currentUser = getCurrentUser();
            EmailResponseDTO email = secureEmailService.receiveAndDecryptEmail(emailId, currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "email", email
            ));
        } catch (Exception e) {
            log.error("Failed to decrypt email", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to decrypt email: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{emailId}")
    public ResponseEntity<?> deleteEmail(@PathVariable String emailId) {
        try {
            User currentUser = getCurrentUser();
            secureEmailService.deleteEmail(emailId, currentUser);
            log.info("Email deleted successfully: {}", emailId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email deleted successfully"
            ));
        } catch (Exception e) {
            log.error("Failed to delete email", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to delete email: " + e.getMessage()
            ));
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }
}
