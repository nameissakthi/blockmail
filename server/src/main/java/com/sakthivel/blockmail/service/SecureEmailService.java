package com.sakthivel.blockmail.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakthivel.blockmail.dto.AttachmentDTO;
import com.sakthivel.blockmail.dto.AttachmentResponseDTO;
import com.sakthivel.blockmail.dto.EmailResponseDTO;
import com.sakthivel.blockmail.dto.SendEmailRequestDTO;
import com.sakthivel.blockmail.model.BlockchainTransaction;
import com.sakthivel.blockmail.model.EmailAttachment;
import com.sakthivel.blockmail.model.EncryptedEmail;
import com.sakthivel.blockmail.model.QuantumKey;
import com.sakthivel.blockmail.model.SecurityLevel;
import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.repository.EncryptedEmailRepository;
import com.sakthivel.blockmail.service.crypto.CryptographyService;
import com.sakthivel.blockmail.service.crypto.CryptographyServiceFactory;
import com.sakthivel.blockmail.service.crypto.EncryptionResult;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Secure Email Service with Quantum Key Distribution integration
 * Orchestrates encryption, key management, blockchain audit, and email delivery
 */
@Service
@Slf4j
public class SecureEmailService {

    private final EncryptedEmailRepository emailRepository;
    private final JavaMailSender mailSender;
    private final KeyLifecycleService keyLifecycleService;
    private final BlockchainService blockchainService;
    private final CryptographyServiceFactory cryptoFactory;
    private final ObjectMapper objectMapper;

    public SecureEmailService(
            @Autowired EncryptedEmailRepository emailRepository,
            @Autowired JavaMailSender mailSender,
            @Autowired KeyLifecycleService keyLifecycleService,
            @Autowired BlockchainService blockchainService,
            @Autowired CryptographyServiceFactory cryptoFactory) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
        this.keyLifecycleService = keyLifecycleService;
        this.blockchainService = blockchainService;
        this.cryptoFactory = cryptoFactory;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send secure email with quantum encryption
     */
    @Transactional
    public EmailResponseDTO sendSecureEmail(SendEmailRequestDTO request, User sender) {
        try {
            log.info("Sending secure email from {} to {} with security level: {}",
                     sender.getEmail(), request.getRecipientEmail(), request.getSecurityLevel());

            // Get cryptography service for selected security level
            CryptographyService cryptoService = cryptoFactory.getService(request.getSecurityLevel());

            // Get or generate quantum key (if quantum security is selected)
            QuantumKey quantumKey = null;
            byte[] encryptionKey;

            if (request.getSecurityLevel() != SecurityLevel.STANDARD_ENCRYPTION) {
                quantumKey = keyLifecycleService.getKeyForEncryption(sender, request.getSecurityLevel());
                encryptionKey = Base64.getDecoder().decode(quantumKey.getKeyMaterial());
                log.debug("Using quantum key: {} for encryption", quantumKey.getKeyId());
            } else {
                // Generate random key for standard encryption
                encryptionKey = new byte[32];
                new Random().nextBytes(encryptionKey);
                log.debug("Using standard encryption without quantum key");
            }

            // Encrypt email content
            byte[] contentBytes = request.getContent().getBytes(StandardCharsets.UTF_8);
            EncryptionResult encryptionResult = cryptoService.encrypt(contentBytes, encryptionKey);

            String encryptedContent = Base64.getEncoder().encodeToString(encryptionResult.getCiphertext());
            String encryptionMetadata = objectMapper.writeValueAsString(encryptionResult.getMetadata());

            // Calculate message hash for integrity
            String messageHash = calculateSHA256(request.getContent());

            // Create encrypted email entity
            EncryptedEmail email = new EncryptedEmail();
            email.setSender(sender);
            email.setRecipientEmail(request.getRecipientEmail());
            email.setSubject(request.getSubject());
            email.setEncryptedContent(encryptedContent);
            email.setSecurityLevel(request.getSecurityLevel());
            email.setQuantumKeyId(quantumKey != null ? quantumKey.getKeyId() : null);
            email.setInitializationVector(encryptionResult.getMetadata().get("iv"));
            email.setEncryptionMetadata(encryptionMetadata);
            email.setMessageHash(messageHash);

            // Process attachments
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                for (AttachmentDTO attachmentDTO : request.getAttachments()) {
                    EmailAttachment attachment = encryptAttachment(attachmentDTO, encryptionKey, cryptoService, quantumKey);
                    attachment.setEmail(email);
                    email.getAttachments().add(attachment);
                }
            }

            // Save to database
            EncryptedEmail savedEmail = emailRepository.save(email);

            // Mark quantum key as used
            if (quantumKey != null) {
                keyLifecycleService.markKeyAsUsed(quantumKey.getKeyId(), sender, savedEmail.getId());
            }

            // Record on blockchain
            BlockchainTransaction blockchainTx = blockchainService.recordEmailVerification(
                    savedEmail.getId(), sender, request.getRecipientEmail(), messageHash);
            savedEmail.setBlockchainTxHash(blockchainTx.getTransactionHash());
            emailRepository.save(savedEmail);

            // Send actual email via SMTP
            sendEmailViaSmtp(savedEmail, encryptionKey);

            log.info("Secure email sent successfully: {}", savedEmail.getId());

            // Return response
            return convertToResponseDTO(savedEmail, request.getContent());

        } catch (Exception e) {
            log.error("Failed to send secure email", e);
            throw new RuntimeException("Failed to send secure email: " + e.getMessage(), e);
        }
    }

    /**
     * Receive and decrypt email
     */
    @Transactional
    public EmailResponseDTO receiveAndDecryptEmail(String emailId, User recipient) {
        try {
            EncryptedEmail email = emailRepository.findById(emailId)
                    .orElseThrow(() -> new RuntimeException("Email not found: " + emailId));

            log.info("Decrypting email {} for user {}", emailId, recipient.getEmail());

            // Get cryptography service
            CryptographyService cryptoService = cryptoFactory.getService(email.getSecurityLevel());

            // Get decryption key - MUST use the same quantum key that was used for encryption
            byte[] decryptionKey;
            if (email.getQuantumKeyId() != null) {
                // Retrieve the exact same quantum key that was used for encryption
                QuantumKey quantumKey = keyLifecycleService.getKeyById(email.getQuantumKeyId());
                if (quantumKey == null) {
                    throw new RuntimeException("Quantum key not found: " + email.getQuantumKeyId());
                }
                decryptionKey = Base64.getDecoder().decode(quantumKey.getKeyMaterial());
                log.debug("Using quantum key {} for decryption", quantumKey.getKeyId());
            } else {
                throw new RuntimeException("Decryption key not available");
            }

            // Decrypt content
            byte[] ciphertext = Base64.getDecoder().decode(email.getEncryptedContent());
            Map<String, String> metadata = objectMapper.readValue(
                    email.getEncryptionMetadata(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));

            byte[] plaintext = cryptoService.decrypt(ciphertext, decryptionKey, metadata);
            String decryptedContent = new String(plaintext, StandardCharsets.UTF_8);

            // Verify integrity
            String computedHash = calculateSHA256(decryptedContent);
            if (!computedHash.equals(email.getMessageHash())) {
                log.warn("Message integrity verification failed for email: {}", emailId);
            }

            // Mark as received
            if (!email.getIsReceived()) {
                email.setIsReceived(true);
                email.setReceivedAt(LocalDateTime.now());
                emailRepository.save(email);
            }

            // Decrypt attachments
            List<AttachmentResponseDTO> decryptedAttachments = email.getAttachments().stream()
                    .map(att -> decryptAttachment(att, decryptionKey, cryptoService))
                    .collect(Collectors.toList());

            // Return decrypted email
            EmailResponseDTO response = convertToResponseDTO(email, decryptedContent);
            response.setAttachments(decryptedAttachments);

            return response;

        } catch (Exception e) {
            log.error("Failed to decrypt email", e);
            throw new RuntimeException("Failed to decrypt email: " + e.getMessage(), e);
        }
    }

    /**
     * Get sent emails for user
     */
    /**
     * Get sent emails for user
     */
    @Transactional(readOnly = true)
    public List<EmailResponseDTO> getSentEmails(User user) {
        List<EncryptedEmail> emails = emailRepository.findBySenderOrderBySentAtDesc(user);
        return emails.stream()
                .filter(email -> !email.getDeletedBySender()) // Exclude emails deleted by sender
                .map(email -> convertToResponseDTO(email, "[Encrypted]"))
                .collect(Collectors.toList());
    }

    /**
     * Get received emails for user
     */
    @Transactional(readOnly = true)
    public List<EmailResponseDTO> getReceivedEmails(String userEmail) {
        List<EncryptedEmail> emails = emailRepository.findByRecipientEmailOrderBySentAtDesc(userEmail);
        return emails.stream()
                .filter(email -> !email.getDeletedByRecipient()) // Exclude emails deleted by recipient
                .map(email -> convertToResponseDTO(email, "[Encrypted]"))
                .collect(Collectors.toList());
    }

    /**
     * Delete email (soft delete)
     * Only marks as deleted for the current user - doesn't affect the other party
     */
    @Transactional
    public void deleteEmail(String emailId, User user) {
        EncryptedEmail email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email not found: " + emailId));

        // Determine if user is sender or recipient
        boolean isSender = email.getSender().getId().equals(user.getId());
        boolean isRecipient = email.getRecipientEmail().equals(user.getEmail());

        if (!isSender && !isRecipient) {
            throw new RuntimeException("Unauthorized: You can only delete your own emails");
        }

        // Soft delete: mark as deleted only for this user
        if (isSender) {
            email.setDeletedBySender(true);
            log.info("Email {} marked as deleted by sender: {}", emailId, user.getEmail());
        }
        if (isRecipient) {
            email.setDeletedByRecipient(true);
            log.info("Email {} marked as deleted by recipient: {}", emailId, user.getEmail());
        }

        emailRepository.save(email);

        // Optional: Permanently delete only if both parties have deleted it
        if (email.getDeletedBySender() && email.getDeletedByRecipient()) {
            emailRepository.delete(email);
            log.info("Email {} permanently deleted (both parties deleted)", emailId);
        }
    }

    /**
     * Encrypt attachment
     */
    private EmailAttachment encryptAttachment(AttachmentDTO dto, byte[] key,
                                             CryptographyService cryptoService, QuantumKey quantumKey) {
        try {
            byte[] fileData = Base64.getDecoder().decode(dto.getBase64Data());
            String fileHash = calculateSHA256(Arrays.toString(fileData));

            EncryptionResult result = cryptoService.encrypt(fileData, key);

            EmailAttachment attachment = new EmailAttachment();
            attachment.setFileName(dto.getFileName());
            attachment.setContentType(dto.getContentType());
            attachment.setFileSize(dto.getFileSize());
            attachment.setEncryptedData(Base64.getEncoder().encodeToString(result.getCiphertext()));
            attachment.setQuantumKeyId(quantumKey != null ? quantumKey.getKeyId() : null);
            attachment.setInitializationVector(result.getMetadata().get("iv"));
            attachment.setFileHash(fileHash);

            return attachment;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt attachment: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt attachment
     */
    private AttachmentResponseDTO decryptAttachment(EmailAttachment attachment, byte[] key,
                                                    CryptographyService cryptoService) {
        try {
            byte[] ciphertext = Base64.getDecoder().decode(attachment.getEncryptedData());

            Map<String, String> metadata = new HashMap<>();
            metadata.put("iv", attachment.getInitializationVector());

            byte[] plaintext = cryptoService.decrypt(ciphertext, key, metadata);

            AttachmentResponseDTO response = new AttachmentResponseDTO();
            response.setId(attachment.getId());
            response.setFileName(attachment.getFileName());
            response.setContentType(attachment.getContentType());
            response.setFileSize(attachment.getFileSize());
            response.setBase64Data(Base64.getEncoder().encodeToString(plaintext));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt attachment: " + e.getMessage(), e);
        }
    }

    /**
     * Send email via SMTP (actual email delivery)
     */
    private void sendEmailViaSmtp(EncryptedEmail email, byte[] encryptionKey) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getRecipientEmail());
            helper.setSubject("[QuMail Encrypted] " + email.getSubject());

            String emailBody = String.format("""
                    <html>
                    <body>
                        <h2>QuMail Secure Message</h2>
                        <p>You have received an encrypted email using Quantum Key Distribution.</p>
                        <p><strong>Security Level:</strong> %s</p>
                        <p><strong>Message ID:</strong> %s</p>
                        <p><strong>Blockchain Verification:</strong> %s</p>
                        <hr>
                        <p><em>Please use QuMail client to decrypt and read this message.</em></p>
                        <p><small>Encrypted Content: %s...</small></p>
                    </body>
                    </html>
                    """,
                    email.getSecurityLevel().getName(),
                    email.getId(),
                    email.getBlockchainTxHash() != null ? email.getBlockchainTxHash() : "Pending",
                    email.getEncryptedContent().substring(0, Math.min(100, email.getEncryptedContent().length())));

            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Email sent via SMTP to: {}", email.getRecipientEmail());

        } catch (Exception e) {
            log.error("Failed to send email via SMTP", e);
            // Don't throw exception - email is already stored in database
        }
    }

    /**
     * Convert entity to DTO
     */
    private EmailResponseDTO convertToResponseDTO(EncryptedEmail email, String decryptedContent) {
        EmailResponseDTO dto = new EmailResponseDTO();
        dto.setId(email.getId());
        dto.setSenderEmail(email.getSender().getEmail());
        dto.setRecipientEmail(email.getRecipientEmail());
        dto.setSubject(email.getSubject());
        dto.setContent(decryptedContent);
        dto.setSecurityLevel(email.getSecurityLevel());
        dto.setSentAt(email.getSentAt());
        dto.setBlockchainTxHash(email.getBlockchainTxHash());
        dto.setVerified(email.getBlockchainTxHash() != null);
        return dto;
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
}

