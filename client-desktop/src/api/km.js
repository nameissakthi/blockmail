/**
 * Quantum Key Distribution (QKD) API Service
 * Handles quantum key operations with the backend
 */

import apiClient from './client.js';
import { API_CONFIG } from './config.js';

export const qkdAPI = {
  /**
   * Obtain quantum keys from Key Manager
   * @param {Object} params - Key request parameters (count, size, etc.)
   * @returns {Promise<Object>} Quantum keys response
   */
  async obtainKeys(params = {}) {
    try {
      const response = await apiClient.post(API_CONFIG.ENDPOINTS.QKD.OBTAIN_KEYS, params);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get quantum key status
   * @returns {Promise<Object>} Key status information
   */
  async getKeyStatus() {
    try {
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.QKD.KEY_STATUS);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Activate a quantum key
   * @param {String} keyId - Key ID to activate
   * @returns {Promise<Object>} Activation response
   */
  async activateKey(keyId) {
    try {
      const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.QKD.ACTIVATE_KEY}/${keyId}`);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Destroy/delete a quantum key
   * @param {String} keyId - Key ID to destroy
   * @returns {Promise<Object>} Destruction response
   */
  async destroyKey(keyId) {
    try {
      const response = await apiClient.delete(`${API_CONFIG.ENDPOINTS.QKD.DESTROY_KEY}/${keyId}`);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get KM service status (for backward compatibility)
   * @returns {Promise<Object>} KM status information
   */
  async getStatus() {
    try {
      // Use key status endpoint to check if service is available
      const response = await this.getKeyStatus();
      return {
        connected: true,
        message: 'QKD service operational',
        ...response
      };
    } catch (error) {
      return {
        connected: false,
        message: 'QKD service unavailable'
      };
    }
  }
};

// Backward compatibility - export as kmAPI as well
export const kmAPI = qkdAPI;

export default qkdAPI;

