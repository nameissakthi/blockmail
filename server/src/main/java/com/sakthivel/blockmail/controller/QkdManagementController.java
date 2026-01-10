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

import com.sakthivel.blockmail.dto.QkdKeyRequestDTO;
import com.sakthivel.blockmail.model.QuantumKey;
import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.service.KeyLifecycleService;
import com.sakthivel.blockmail.service.UserService;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/qkd")
@Slf4j
public class QkdManagementController {
    private final KeyLifecycleService keyLifecycleService;
    private final UserService userService;
    public QkdManagementController(
            @Autowired KeyLifecycleService keyLifecycleService,
            @Autowired UserService userService) {
        this.keyLifecycleService = keyLifecycleService;
        this.userService = userService;
    }
    @PostMapping("/obtain-keys")
    public ResponseEntity<?> obtainQuantumKeys(@RequestBody QkdKeyRequestDTO request) {
        try {
            User currentUser = getCurrentUser();
            List<QuantumKey> keys = keyLifecycleService.obtainQuantumKeys(
                    currentUser, 
                    request.getNumberOfKeys(), 
                    request.getKeySize()
            );
            log.info("Obtained {} quantum keys for user: {}", keys.size(), currentUser.getEmail());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully obtained " + keys.size() + " quantum keys",
                    "keys", keys.stream().map(k -> Map.of(
                            "keyId", k.getKeyId(),
                            "keyMaterial", k.getKeyMaterial(), // Include the actual key material
                            "keySize", k.getKeySize(),
                            "status", k.getStatus(),
                            "obtainedAt", k.getObtainedAt(),
                            "expiresAt", k.getExpiresAt()
                    )).toList()
            ));
        } catch (Exception e) {
            log.error("Failed to obtain quantum keys", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to obtain keys: " + e.getMessage()
            ));
        }
    }
    @PostMapping("/activate-key/{keyId}")
    public ResponseEntity<?> activateKey(@PathVariable String keyId) {
        try {
            User currentUser = getCurrentUser();
            QuantumKey key = keyLifecycleService.activateKey(keyId, currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Key activated successfully",
                    "key", Map.of(
                            "keyId", key.getKeyId(),
                            "status", key.getStatus()
                    )
            ));
        } catch (Exception e) {
            log.error("Failed to activate key", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/key-status")
    public ResponseEntity<?> getKeyStatus() {
        try {
            User currentUser = getCurrentUser();
            long activeCount = keyLifecycleService.getActiveKeyCount(currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "activeKeys", activeCount,
                    "email", currentUser.getEmail()
            ));
        } catch (Exception e) {
            log.error("Failed to get key status", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    @DeleteMapping("/destroy-key/{keyId}")
    public ResponseEntity<?> destroyKey(@PathVariable String keyId) {
        try {
            User currentUser = getCurrentUser();
            keyLifecycleService.destroyKey(keyId, currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Key destroyed successfully"
            ));
        } catch (Exception e) {
            log.error("Failed to destroy key", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }
}
