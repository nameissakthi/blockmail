/**
 * Standard Encryption Service
 * Security Level 4: No quantum security
 * Uses standard AES encryption
 */

export class StandardEncryption {
  /**
   * Encrypt message using standard AES
   * @param {String} message - Message to encrypt
   * @param {String} password - Password for encryption
   * @returns {Object} Encrypted data and metadata
   */
  async encrypt(message, password) {
    if (!message || !password) {
      throw new Error('Message and password are required');
    }

    try {
      // Derive key from password
      const key = await this.deriveKey(password);

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
        algorithm: 'AES-256-GCM',
        securityLevel: 4
      };
    } catch (error) {
      throw new Error(`Encryption failed: ${error.message}`);
    }
  }

  /**
   * Decrypt message using standard AES
   * @param {Object} encryptedData - Encrypted data object
   * @param {String} password - Password used for encryption
   * @returns {String} Decrypted message
   */
  async decrypt(encryptedData, password) {
    if (!encryptedData || !password) {
      throw new Error('Encrypted data and password are required');
    }

    try {
      // Derive key from password
      const key = await this.deriveKey(password);

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
   * Derive key from password using PBKDF2
   */
  async deriveKey(password) {
    const encoder = new TextEncoder();
    const passwordData = encoder.encode(password);

    // Import password as key material
    const keyMaterial = await crypto.subtle.importKey(
      'raw',
      passwordData,
      { name: 'PBKDF2' },
      false,
      ['deriveBits', 'deriveKey']
    );

    // Derive key using PBKDF2
    const salt = encoder.encode('blockmail-salt'); // In production, use random salt
    const key = await crypto.subtle.deriveKey(
      {
        name: 'PBKDF2',
        salt: salt,
        iterations: 100000,
        hash: 'SHA-256'
      },
      keyMaterial,
      { name: 'AES-GCM', length: 256 },
      false,
      ['encrypt', 'decrypt']
    );

    return key;
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
}

export default new StandardEncryption();

