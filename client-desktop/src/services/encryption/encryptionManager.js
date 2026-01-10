/**
 * Encryption Manager
 * Manages encryption/decryption operations across different security levels
 */

import otpEncryption from './otp.js';
import quantumAesEncryption from './quantumAes.js';
import standardEncryption from './standard.js';
import { SECURITY_LEVELS } from './securityLevels.js';

class EncryptionManager {
  /**
   * Encrypt message based on security level
   * @param {String} message - Message to encrypt
   * @param {Number} securityLevel - Security level (1-4)
   * @param {String} key - Encryption key (quantum key for levels 1-2, password for level 4)
   * @returns {Promise<Object>} Encrypted data
   */
  async encrypt(message, securityLevel, key) {
    if (!message) {
      throw new Error('Message is required');
    }

    switch (securityLevel) {
      case 1: // Quantum Secure (OTP)
        if (!key) {
          throw new Error('Quantum key is required for OTP encryption');
        }
        return otpEncryption.encrypt(message, key);

      case 2: // Quantum-aided AES
        if (!key) {
          throw new Error('Quantum key is required for Quantum-AES encryption');
        }
        return await quantumAesEncryption.encrypt(message, key);

      case 3: // Post-Quantum Cryptography
        // TODO: Implement PQC encryption (e.g., Kyber)
        console.warn('PQC encryption not yet implemented, falling back to Quantum-AES');
        if (!key) {
          throw new Error('Quantum key is required for PQC encryption (currently using Quantum-AES)');
        }
        return await quantumAesEncryption.encrypt(message, key);

      case 4: // Standard
        if (!key) {
          throw new Error('Password is required for standard encryption');
        }
        return await standardEncryption.encrypt(message, key);

      default:
        throw new Error(`Invalid security level: ${securityLevel}`);
    }
  }

  /**
   * Decrypt message based on security level
   * @param {Object} encryptedData - Encrypted data object
   * @param {String} key - Decryption key
   * @returns {Promise<String>} Decrypted message
   */
  async decrypt(encryptedData, key) {
    if (!encryptedData || !key) {
      throw new Error('Encrypted data and key are required');
    }

    const securityLevel = encryptedData.securityLevel;

    switch (securityLevel) {
      case 1: // Quantum Secure (OTP)
        return otpEncryption.decrypt(encryptedData.ciphertext, key);

      case 2: // Quantum-aided AES
        return await quantumAesEncryption.decrypt(encryptedData, key);

      case 3: // Post-Quantum Cryptography
        // TODO: Implement PQC decryption
        console.warn('PQC decryption not yet implemented, falling back to Quantum-AES');
        return await quantumAesEncryption.decrypt(encryptedData, key);

      case 4: // Standard
        return await standardEncryption.decrypt(encryptedData, key);

      default:
        throw new Error(`Invalid security level: ${securityLevel}`);
    }
  }

  /**
   * Get security level information
   * @param {Number} levelId - Security level ID
   * @returns {Object} Security level details
   */
  getSecurityLevel(levelId) {
    return Object.values(SECURITY_LEVELS).find(level => level.id === levelId);
  }

  /**
   * Get all available security levels
   * @returns {Array} List of security levels
   */
  getAllSecurityLevels() {
    return Object.values(SECURITY_LEVELS);
  }
}

export default new EncryptionManager();

