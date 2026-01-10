/**
 * Blockchain API Service
 * Handles blockchain verification operations
 */

import apiClient from './client.js';
import { API_CONFIG } from './config.js';

export const blockchainAPI = {
  /**
   * Verify blockchain transaction
   * @param {String} txHash - Transaction hash
   * @returns {Promise<Object>} Verification response
   */
  async verifyTransaction(txHash) {
    try {
      const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.BLOCKCHAIN.VERIFY}/${txHash}`);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get transaction details
   * @param {String} txHash - Transaction hash
   * @returns {Promise<Object>} Transaction details
   */
  async getTransaction(txHash) {
    try {
      const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.BLOCKCHAIN.TRANSACTION}/${txHash}`);
      return response;
    } catch (error) {
      throw error;
    }
  }
};

export default blockchainAPI;

