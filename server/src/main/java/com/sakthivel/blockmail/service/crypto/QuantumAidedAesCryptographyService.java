package com.sakthivel.blockmail.service.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * Level 2: Quantum-aided AES
 * Uses quantum key as seed for AES encryption
 */
@Service
@Slf4j
public class QuantumAidedAesCryptographyService implements CryptographyService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    @Override
    public EncryptionResult encrypt(byte[] plaintext, byte[] quantumKey) {
        try {
            // Derive AES key from quantum key using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] aesKeyBytes = digest.digest(quantumKey);

            // Use only 256 bits for AES-256
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, 0, 32, "AES");

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(plaintext);

            EncryptionResult result = new EncryptionResult();
            result.setCiphertext(ciphertext);
            result.addMetadata("algorithm", "AES-256-GCM");
            result.addMetadata("iv", Base64.getEncoder().encodeToString(iv));
            result.addMetadata("quantumSeeded", "true");
            result.addMetadata("tagLength", String.valueOf(GCM_TAG_LENGTH));

            log.debug("Quantum-aided AES encryption completed: {} bytes", ciphertext.length);
            return result;

        } catch (Exception e) {
            log.error("Quantum-aided AES encryption failed", e);
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] quantumKey, Map<String, String> metadata) {
        try {
            // Derive AES key from quantum key using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] aesKeyBytes = digest.digest(quantumKey);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, 0, 32, "AES");

            // Get IV from metadata
            String ivBase64 = metadata.get("iv");
            if (ivBase64 == null) {
                throw new IllegalArgumentException("IV not found in metadata");
            }
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            // Decrypt
            byte[] plaintext = cipher.doFinal(ciphertext);

            log.debug("Quantum-aided AES decryption completed: {} bytes", plaintext.length);
            return plaintext;

        } catch (Exception e) {
            log.error("Quantum-aided AES decryption failed", e);
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSecurityLevel() {
        return "QUANTUM_AIDED_AES";
    }

    @Override
    public boolean isKeySizeValid(int keySizeInBits) {
        // Accept any size >= 256 bits (will be hashed to 256)
        return keySizeInBits >= 256;
    }
}

