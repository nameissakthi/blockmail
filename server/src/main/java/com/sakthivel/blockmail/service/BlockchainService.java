package com.sakthivel.blockmail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakthivel.blockmail.model.BlockchainTransaction;
import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.repository.BlockchainTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Blockchain Service for auditing and verification
 * Mock implementation - can be replaced with actual Hyperledger/Ethereum integration
 */
@Service
@Slf4j
public class BlockchainService {

    private final BlockchainTransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    public BlockchainService(@Autowired BlockchainTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Record key audit event on blockchain
     */
    @Transactional
    public BlockchainTransaction recordKeyAudit(String quantumKeyId, User user, String action, String metadata) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("keyId", quantumKeyId);
            auditData.put("userId", user.getId());
            auditData.put("userEmail", user.getEmail());
            auditData.put("action", action);
            auditData.put("timestamp", System.currentTimeMillis());
            auditData.put("metadata", metadata);

            String dataJson = objectMapper.writeValueAsString(auditData);
            String dataHash = calculateSHA256(dataJson);

            // Mock blockchain transaction
            String txHash = generateMockTransactionHash();

            BlockchainTransaction transaction = new BlockchainTransaction();
            transaction.setTransactionHash(txHash);
            transaction.setTransactionType("KEY_AUDIT");
            transaction.setDataHash(dataHash);
            transaction.setReferenceId(quantumKeyId);
            transaction.setUser(user);
            transaction.setBlockchainNetwork("HYPERLEDGER_MOCK");
            transaction.setVerified(true); // Mock verification
            transaction.setVerifiedAt(LocalDateTime.now());
            transaction.setBlockNumber(System.currentTimeMillis() / 1000); // Mock block number
            transaction.setMetadata(dataJson);

            BlockchainTransaction saved = transactionRepository.save(transaction);

            log.info("Key audit recorded on blockchain: {} - {}", quantumKeyId, txHash);
            return saved;

        } catch (Exception e) {
            log.error("Failed to record key audit on blockchain", e);
            throw new RuntimeException("Blockchain recording failed: " + e.getMessage(), e);
        }
    }

    /**
     * Record email verification on blockchain
     */
    @Transactional
    public BlockchainTransaction recordEmailVerification(String emailId, User sender,
                                                         String recipientEmail, String messageHash) {
        try {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("emailId", emailId);
            emailData.put("senderId", sender.getId());
            emailData.put("senderEmail", sender.getEmail());
            emailData.put("recipientEmail", recipientEmail);
            emailData.put("messageHash", messageHash);
            emailData.put("timestamp", System.currentTimeMillis());

            String dataJson = objectMapper.writeValueAsString(emailData);
            String dataHash = calculateSHA256(dataJson);

            // Mock blockchain transaction
            String txHash = generateMockTransactionHash();

            BlockchainTransaction transaction = new BlockchainTransaction();
            transaction.setTransactionHash(txHash);
            transaction.setTransactionType("EMAIL_VERIFICATION");
            transaction.setDataHash(dataHash);
            transaction.setReferenceId(emailId);
            transaction.setUser(sender);
            transaction.setBlockchainNetwork("ETHEREUM_MOCK");
            transaction.setVerified(true); // Mock verification
            transaction.setVerifiedAt(LocalDateTime.now());
            transaction.setBlockNumber(System.currentTimeMillis() / 1000);
            transaction.setMetadata(dataJson);

            BlockchainTransaction saved = transactionRepository.save(transaction);

            log.info("Email verification recorded on blockchain: {} - {}", emailId, txHash);
            return saved;

        } catch (Exception e) {
            log.error("Failed to record email verification on blockchain", e);
            throw new RuntimeException("Blockchain recording failed: " + e.getMessage(), e);
        }
    }

    /**
     * Verify blockchain transaction
     */
    @Transactional(readOnly = true)
    public boolean verifyTransaction(String transactionHash) {
        // In a real implementation, this would query the blockchain network
        // For mock, we check our local database
        return transactionRepository.findByTransactionHash(transactionHash)
                .map(BlockchainTransaction::getVerified)
                .orElse(false);
    }

    /**
     * Get blockchain transaction details
     */
    @Transactional(readOnly = true)
    public BlockchainTransaction getTransaction(String transactionHash) {
        return transactionRepository.findByTransactionHash(transactionHash)
                .orElseThrow(() -> new RuntimeException("Blockchain transaction not found: " + transactionHash));
    }

    /**
     * Calculate SHA-256 hash
     */
    private String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hash", e);
        }
    }

    /**
     * Generate mock transaction hash
     */
    private String generateMockTransactionHash() {
        return "0x" + UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}

