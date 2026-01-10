package com.sakthivel.blockmail.service.crypto;

import java.util.Map;

/**
 * Interface for cryptography services
 * Strategy pattern for different security levels
 */
public interface CryptographyService {

    /**
     * Encrypt data using the specific cryptography implementation
     *
     * @param plaintext Data to encrypt
     * @param key Encryption key (quantum key material)
     * @return Encrypted result with ciphertext and metadata
     */
    EncryptionResult encrypt(byte[] plaintext, byte[] key);

    /**
     * Decrypt data using the specific cryptography implementation
     *
     * @param ciphertext Encrypted data
     * @param key Decryption key
     * @param metadata Encryption metadata (IV, algorithm params, etc.)
     * @return Decrypted plaintext
     */
    byte[] decrypt(byte[] ciphertext, byte[] key, Map<String, String> metadata);

    /**
     * Get the security level this service implements
     */
    String getSecurityLevel();

    /**
     * Validate if key size is appropriate for this encryption method
     */
    boolean isKeySizeValid(int keySizeInBits);
}
