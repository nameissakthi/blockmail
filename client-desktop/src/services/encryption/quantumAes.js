/**
 * Quantum-aided AES Encryption Service
 * Security Level 2: Uses quantum keys to seed AES-256 encryption
 */

export class QuantumAESEncryption {
  /**
   * Encrypt message using AES with quantum key seeding
   * @param {String} message - Message to encrypt
   * @param {String} quantumKey - Quantum key for seeding
   * @returns {Object} Encrypted data and metadata
   */
  async encrypt(message, quantumKey) {
    if (!message || !quantumKey) {
      throw new Error('Message and quantum key are required');
    }

    try {
      // Convert quantum key to CryptoKey for AES
      const key = await this.deriveAESKey(quantumKey);

      // Generate random IV
      const iv = crypto.getRandomValues(new Uint8Array(12));

      // Encrypt message
      const encoder = new TextEncoder();
      const data = encoder.encode(message);

      const encrypted = await crypto.subtle.encrypt(
        {
          name: 'AES-GCM',
          iv: iv
        },
        key,
        data
      );

      return {
        ciphertext: this.arrayBufferToHex(encrypted),
        iv: this.arrayBufferToHex(iv),
        keyId: this.generateKeyId(quantumKey),
        algorithm: 'AES-256-GCM',
        securityLevel: 2
      };
    } catch (error) {
      throw new Error(`Encryption failed: ${error.message}`);
    }
  }

  /**
   * Decrypt message using AES with quantum key
   * @param {Object} encryptedData - Encrypted data object
   * @param {String} quantumKey - Quantum key used for encryption
   * @returns {String} Decrypted message
   */
  async decrypt(encryptedData, quantumKey) {
    if (!encryptedData || !quantumKey) {
      throw new Error('Encrypted data and quantum key are required');
    }

    try {
      // Derive AES key from quantum key
      const key = await this.deriveAESKey(quantumKey);

      // Convert hex to ArrayBuffer
      const ciphertext = this.hexToArrayBuffer(encryptedData.ciphertext);
      const iv = this.hexToArrayBuffer(encryptedData.iv);

      // Decrypt message
      const decrypted = await crypto.subtle.decrypt(
        {
          name: 'AES-GCM',
          iv: iv
        },
        key,
        ciphertext
      );

      const decoder = new TextDecoder();
      return decoder.decode(decrypted);
    } catch (error) {
      throw new Error(`Decryption failed: ${error.message}`);
    }
  }

  /**
   * Derive AES key from quantum key
   */
  async deriveAESKey(quantumKey) {
    try {
      // Ensure quantum key is a string
      if (typeof quantumKey !== 'string') {
        throw new Error('Quantum key must be a string');
      }

      // Remove any whitespace or newlines
      const cleanKey = quantumKey.replace(/\s+/g, '');
      
      // If the key is base64 encoded, decode it first
      let keyHex = cleanKey;
      if (!/^[0-9a-fA-F]+$/.test(cleanKey)) {
        // Might be base64, convert to hex
        const base64Decoded = atob(cleanKey);
        keyHex = Array.from(base64Decoded).map(c => 
          c.charCodeAt(0).toString(16).padStart(2, '0')
        ).join('');
      }
      
      // Use first 256 bits (64 hex characters) for AES-256
      const key256bits = keyHex.substring(0, 64);
      
      // Ensure we have exactly 64 hex characters (256 bits)
      if (key256bits.length !== 64) {
        throw new Error(`Invalid key length: expected 64 hex chars (256 bits), got ${key256bits.length}`);
      }

      // Convert to ArrayBuffer
      const keyMaterial = this.hexToArrayBuffer(key256bits);
      
      // Verify we have 32 bytes (256 bits)
      if (keyMaterial.byteLength !== 32) {
        throw new Error(`Invalid key size: expected 32 bytes (256 bits), got ${keyMaterial.byteLength}`);
      }

      // Import key material
      const key = await crypto.subtle.importKey(
        'raw',
        keyMaterial,
        { name: 'AES-GCM' },
        false,
        ['encrypt', 'decrypt']
      );

      return key;
    } catch (error) {
      throw new Error(`Failed to derive AES key: ${error.message}`);
    }
  }

  // Helper functions
  arrayBufferToHex(buffer) {
    return Array.from(new Uint8Array(buffer))
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
  }

  hexToArrayBuffer(hex) {
    const bytes = new Uint8Array(hex.length / 2);
    for (let i = 0; i < hex.length; i += 2) {
      bytes[i / 2] = parseInt(hex.substr(i, 2), 16);
    }
    return bytes.buffer;
  }

  generateKeyId(key) {
    let hash = 0;
    for (let i = 0; i < Math.min(key.length, 100); i++) {
      const char = key.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }
    return Math.abs(hash).toString(16);
  }
}

export default new QuantumAESEncryption();

