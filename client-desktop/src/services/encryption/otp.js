/**
 * One Time Pad (OTP) Encryption Service
 * Security Level 1: Quantum Secure
 * Provides unconditional security using quantum keys
 */

export class OTPEncryption {
  /**
   * Encrypt message using One Time Pad
   * @param {String} message - Message to encrypt
   * @param {String} key - Quantum key (must be at least as long as message)
   * @returns {Object} Encrypted data and metadata
   */
  encrypt(message, key) {
    if (!message || !key) {
      throw new Error('Message and key are required');
    }

    try {
      const messageBytes = this.stringToBytes(message);
      
      // Clean and convert key (handle both hex and base64)
      const cleanKey = key.replace(/\s+/g, '');
      let keyBytes;
      
      // Try to parse as hex first
      if (/^[0-9a-fA-F]+$/.test(cleanKey)) {
        keyBytes = this.hexToBytes(cleanKey);
      } else {
        // Try base64
        try {
          const base64Decoded = atob(cleanKey);
          keyBytes = new Uint8Array(Array.from(base64Decoded).map(c => c.charCodeAt(0)));
        } catch (e) {
          throw new Error('Invalid key format: must be hex or base64');
        }
      }

      if (keyBytes.length < messageBytes.length) {
        throw new Error(`Key too short: need at least ${messageBytes.length} bytes, got ${keyBytes.length}`);
      }

      // XOR message with key (One Time Pad)
      const encrypted = new Uint8Array(messageBytes.length);
      for (let i = 0; i < messageBytes.length; i++) {
        encrypted[i] = messageBytes[i] ^ keyBytes[i];
      }

      return {
        ciphertext: this.bytesToHex(encrypted),
        keyId: this.generateKeyId(key),
        algorithm: 'OTP',
        securityLevel: 1
      };
    } catch (error) {
      throw new Error(`OTP encryption failed: ${error.message}`);
    }
  }

  /**
   * Decrypt message using One Time Pad
   * @param {String} ciphertext - Encrypted message
   * @param {String} key - Quantum key used for encryption
   * @returns {String} Decrypted message
   */
  decrypt(ciphertext, key) {
    if (!ciphertext || !key) {
      throw new Error('Ciphertext and key are required');
    }

    const ciphertextBytes = this.hexToBytes(ciphertext);
    const keyBytes = this.hexToBytes(key);

    // XOR ciphertext with key (One Time Pad)
    const decrypted = new Uint8Array(ciphertextBytes.length);
    for (let i = 0; i < ciphertextBytes.length; i++) {
      decrypted[i] = ciphertextBytes[i] ^ keyBytes[i];
    }

    return this.bytesToString(decrypted);
  }

  // Helper functions
  stringToBytes(str) {
    return new TextEncoder().encode(str);
  }

  bytesToString(bytes) {
    return new TextDecoder().decode(bytes);
  }

  hexToBytes(hex) {
    const bytes = new Uint8Array(hex.length / 2);
    for (let i = 0; i < hex.length; i += 2) {
      bytes[i / 2] = parseInt(hex.substr(i, 2), 16);
    }
    return bytes;
  }

  bytesToHex(bytes) {
    return Array.from(bytes)
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
  }

  generateKeyId(key) {
    // Generate a simple hash-based key ID
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      const char = key.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }
    return Math.abs(hash).toString(16);
  }
}

export default new OTPEncryption();

