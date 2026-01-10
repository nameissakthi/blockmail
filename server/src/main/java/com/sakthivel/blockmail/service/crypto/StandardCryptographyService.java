package com.sakthivel.blockmail.service.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * Level 4: Standard Encryption (No Quantum Security)
 * Uses standard AES-256-GCM without quantum keys
 */
@Service
@Slf4j
public class StandardCryptographyService implements CryptographyService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_SIZE = 256;

    @Override
    public EncryptionResult encrypt(byte[] plaintext, byte[] key) {
        try {
            SecretKey secretKey;

            if (key == null || key.length == 0) {
                // Generate random AES key
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(AES_KEY_SIZE);
                secretKey = keyGen.generateKey();
            } else {
                // Use provided key (truncate or pad to 32 bytes)
                byte[] aesKeyBytes = new byte[32];
                System.arraycopy(key, 0, aesKeyBytes, 0, Math.min(key.length, 32));
                secretKey = new SecretKeySpec(aesKeyBytes, "AES");
            }

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(plaintext);

            EncryptionResult result = new EncryptionResult();
            result.setCiphertext(ciphertext);
            result.addMetadata("algorithm", "AES-256-GCM");
            result.addMetadata("iv", Base64.getEncoder().encodeToString(iv));
            result.addMetadata("quantumSecure", "false");

            log.debug("Standard AES encryption completed: {} bytes", ciphertext.length);
            return result;

        } catch (Exception e) {
            log.error("Standard encryption failed", e);
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key, Map<String, String> metadata) {
        try {
            // Ensure key is 256 bits
            byte[] aesKeyBytes = new byte[32];
            System.arraycopy(key, 0, aesKeyBytes, 0, Math.min(key.length, 32));
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

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

            log.debug("Standard AES decryption completed: {} bytes", plaintext.length);
            return plaintext;

        } catch (Exception e) {
            log.error("Standard decryption failed", e);
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSecurityLevel() {
        return "STANDARD_ENCRYPTION";
    }

    @Override
    public boolean isKeySizeValid(int keySizeInBits) {
        return keySizeInBits >= 128;
    }
}

