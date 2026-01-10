package com.sakthivel.blockmail.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakthivel.blockmail.config.QkdKeyManagerConfig;
import com.sakthivel.blockmail.dto.QkdKeyMaterialDTO;
import com.sakthivel.blockmail.dto.QkdKeyRequestDTO;
import com.sakthivel.blockmail.model.KeyAuditLog;
import com.sakthivel.blockmail.model.KeyStatus;
import com.sakthivel.blockmail.model.QuantumKey;
import com.sakthivel.blockmail.model.SecurityLevel;
import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.repository.KeyAuditLogRepository;
import com.sakthivel.blockmail.repository.QuantumKeyRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Key Lifecycle Management Service
 * Manages quantum key lifecycle: RESERVED -> ACTIVE -> USED -> EXPIRED -> DESTROYED
 */
@Service
@Slf4j
public class KeyLifecycleService {

    private final QuantumKeyRepository quantumKeyRepository;
    private final KeyAuditLogRepository auditLogRepository;
    private final QkdClientService qkdClientService;
    private final QkdKeyManagerConfig config;

    public KeyLifecycleService(
            @Autowired QuantumKeyRepository quantumKeyRepository,
            @Autowired KeyAuditLogRepository auditLogRepository,
            @Autowired QkdClientService qkdClientService,
            @Autowired QkdKeyManagerConfig config) {
        this.quantumKeyRepository = quantumKeyRepository;
        this.auditLogRepository = auditLogRepository;
        this.qkdClientService = qkdClientService;
        this.config = config;
    }

    /**
     * Obtain quantum keys from KM and store them
     */
    @Transactional
    public List<QuantumKey> obtainQuantumKeys(User user, int count, Integer keySize) {
        log.info("Obtaining {} quantum keys for user: {}", count, user.getEmail());

        QkdKeyRequestDTO request = new QkdKeyRequestDTO();
        request.setNumberOfKeys(count);
        request.setKeySize(keySize != null ? keySize : config.getDefaultKeySize());

        List<QkdKeyMaterialDTO> keyMaterials = qkdClientService.getQuantumKeys(request);

        return keyMaterials.stream().map(keyMaterial -> {
            QuantumKey quantumKey = new QuantumKey();
            quantumKey.setKeyId(keyMaterial.getKeyId());
            quantumKey.setKeyMaterial(keyMaterial.getKey());
            quantumKey.setKeySize(keyMaterial.getKeySize());
            quantumKey.setStatus(KeyStatus.RESERVED);
            quantumKey.setOwner(user);
            quantumKey.setKmSourceId(keyMaterial.getSourceKmId());
            quantumKey.setExpiresAt(LocalDateTime.now().plusSeconds(config.getKeyLifetimeSeconds()));

            QuantumKey saved = quantumKeyRepository.save(quantumKey);

            // Audit log
            logKeyAction(saved, user, "OBTAINED", null);

            return saved;
        }).toList();
    }

    /**
     * Activate a reserved quantum key for use
     */
    @Transactional
    public QuantumKey activateKey(String keyId, User user) {
        Optional<QuantumKey> keyOpt = quantumKeyRepository.findByKeyId(keyId);

        if (keyOpt.isEmpty()) {
            throw new RuntimeException("Quantum key not found: " + keyId);
        }

        QuantumKey key = keyOpt.get();

        if (!key.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to quantum key");
        }

        if (key.getStatus() != KeyStatus.RESERVED) {
            throw new RuntimeException("Key is not in RESERVED state");
        }

        key.setStatus(KeyStatus.ACTIVE);
        QuantumKey saved = quantumKeyRepository.save(key);

        logKeyAction(saved, user, "ACTIVATED", null);

        return saved;
    }

    /**
     * Get an active quantum key for encryption
     */
    @Transactional
    public QuantumKey getKeyForEncryption(User user, SecurityLevel securityLevel) {
        // Check if user has active keys
        Optional<QuantumKey> activeKey = quantumKeyRepository
            .findFirstByOwnerAndStatusOrderByObtainedAtAsc(user, KeyStatus.ACTIVE);

        if (activeKey.isEmpty()) {
            // No active keys, obtain new ones and activate one
            log.info("No active keys available, obtaining new quantum keys for user: {}", user.getEmail());
            List<QuantumKey> newKeys = obtainQuantumKeys(user, config.getKeyPoolSize(), null);

            if (!newKeys.isEmpty()) {
                QuantumKey key = newKeys.get(0);
                return activateKey(key.getKeyId(), user);
            } else {
                throw new RuntimeException("Failed to obtain quantum keys from KM");
            }
        }

        return activeKey.get();
    }

    /**
     * Mark key as used after encryption operation
     */
    @Transactional
    public void markKeyAsUsed(String keyId, User user, String emailId) {
        Optional<QuantumKey> keyOpt = quantumKeyRepository.findByKeyId(keyId);

        if (keyOpt.isEmpty()) {
            throw new RuntimeException("Quantum key not found: " + keyId);
        }

        QuantumKey key = keyOpt.get();
        key.setStatus(KeyStatus.USED);
        key.setUsedAt(LocalDateTime.now());
        quantumKeyRepository.save(key);

        logKeyAction(key, user, "USED", emailId);
    }

    /**
     * Cleanup expired keys (scheduled task)
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void cleanupExpiredKeys() {
        LocalDateTime now = LocalDateTime.now();
        List<QuantumKey> expiredKeys = quantumKeyRepository
            .findByStatusAndExpiresAtBefore(KeyStatus.ACTIVE, now);

        for (QuantumKey key : expiredKeys) {
            key.setStatus(KeyStatus.EXPIRED);
            quantumKeyRepository.save(key);
            logKeyAction(key, key.getOwner(), "EXPIRED", null);
        }

        if (!expiredKeys.isEmpty()) {
            log.info("Expired {} quantum keys", expiredKeys.size());
        }
    }

    /**
     * Destroy used or expired keys (secure deletion)
     */
    @Transactional
    public void destroyKey(String keyId, User user) {
        Optional<QuantumKey> keyOpt = quantumKeyRepository.findByKeyId(keyId);

        if (keyOpt.isEmpty()) {
            return;
        }

        QuantumKey key = keyOpt.get();

        if (!key.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to quantum key");
        }

        if (key.getStatus() != KeyStatus.USED && key.getStatus() != KeyStatus.EXPIRED) {
            throw new RuntimeException("Only USED or EXPIRED keys can be destroyed");
        }

        // Securely overwrite key material
        key.setKeyMaterial("DESTROYED");
        key.setStatus(KeyStatus.DESTROYED);
        quantumKeyRepository.save(key);

        logKeyAction(key, user, "DESTROYED", null);
    }

    /**
     * Get quantum key by ID (used for decryption)
     */
    @Transactional(readOnly = true)
    public QuantumKey getKeyById(String keyId) {
        Optional<QuantumKey> keyOpt = quantumKeyRepository.findByKeyId(keyId);
        return keyOpt.orElse(null);
    }

    /**
     * Get key statistics for user
     */
    public long getActiveKeyCount(User user) {
        return quantumKeyRepository.countByOwnerAndStatus(user, KeyStatus.ACTIVE);
    }

    /**
     * Ensure minimum key pool size
     */
    @Scheduled(fixedRate = 600000) // Run every 10 minutes
    @Transactional
    public void maintainKeyPool() {
        // This would need to iterate through users in a real implementation
        // For now, it's a placeholder for the concept
        log.debug("Key pool maintenance check (placeholder for multi-user implementation)");
    }

    /**
     * Log key action to audit trail
     */
    private void logKeyAction(QuantumKey key, User user, String action, String emailId) {
        KeyAuditLog auditLog = new KeyAuditLog();
        auditLog.setQuantumKeyId(key.getKeyId());
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEmailId(emailId);

        auditLogRepository.save(auditLog);

        log.debug("Key action logged: {} - {} - {}", key.getKeyId(), action, user.getEmail());
    }
}

