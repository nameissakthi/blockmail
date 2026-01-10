package com.sakthivel.blockmail.service.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Level 1: Quantum Secure - One Time Pad (OTP)
 * Provides perfect secrecy using quantum keys
 */
@Service
@Slf4j
public class OtpCryptographyService implements CryptographyService {

    @Override
    public EncryptionResult encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length > key.length) {
            throw new IllegalArgumentException(
                "OTP requires key length >= plaintext length. Key: " + key.length +
                " bytes, Plaintext: " + plaintext.length + " bytes");
        }

        byte[] ciphertext = new byte[plaintext.length];

        // XOR operation for OTP
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ key[i]);
        }

        EncryptionResult result = new EncryptionResult();
        result.setCiphertext(ciphertext);
        result.addMetadata("algorithm", "OTP");
        result.addMetadata("keyLength", String.valueOf(key.length));
        result.addMetadata("plaintextLength", String.valueOf(plaintext.length));

        log.debug("OTP encryption completed: {} bytes", ciphertext.length);
        return result;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key, Map<String, String> metadata) {
        if (ciphertext.length > key.length) {
            throw new IllegalArgumentException(
                "OTP requires key length >= ciphertext length. Key: " + key.length +
                " bytes, Ciphertext: " + ciphertext.length + " bytes");
        }

        byte[] plaintext = new byte[ciphertext.length];

        // XOR operation for OTP (encryption and decryption are identical)
        for (int i = 0; i < ciphertext.length; i++) {
            plaintext[i] = (byte) (ciphertext[i] ^ key[i]);
        }

        log.debug("OTP decryption completed: {} bytes", plaintext.length);
        return plaintext;
    }

    @Override
    public String getSecurityLevel() {
        return "QUANTUM_SECURE_OTP";
    }

    @Override
    public boolean isKeySizeValid(int keySizeInBits) {
        // OTP requires key size >= message size, so we accept any size
        return keySizeInBits >= 128; // Minimum 128 bits
    }
}

