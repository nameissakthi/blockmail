/**
 * Email API Service
 * Handles quantum-secured email operations
 */

import apiClient from './client.js';
import { API_CONFIG } from './config.js';

export const emailAPI = {
  /**
   * Get received emails (inbox)
   * @param {Object} params - Query parameters (page, limit, etc.)
   * @returns {Promise<Object>} List of received emails
   */
  async getInbox(params = {}) {
    try {
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.EMAIL.RECEIVED, { params });
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get sent emails
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} List of sent emails
   */
  async getSent(params = {}) {
    try {
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.EMAIL.SENT, { params });
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get and decrypt specific email by ID
   * @param {String} emailId - Email ID
   * @returns {Promise<Object>} Decrypted email details
   */
  async getEmail(emailId) {
    try {
      const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.EMAIL.DECRYPT}/${emailId}`);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Send quantum-secured email
   * @param {Object} emailData - Email data (to, subject, body, attachments, securityLevel)
   * @returns {Promise<Object>} Send response with transaction hash
   */
  async sendEmail(emailData) {
    try {
      const response = await apiClient.post(API_CONFIG.ENDPOINTS.EMAIL.SEND, emailData);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Delete email
   * @param {String} emailId - Email ID
   * @returns {Promise<Object>} Delete response
   */
  async deleteEmail(emailId) {
    try {
      const response = await apiClient.delete(`/api/quantum-email/delete/${emailId}`);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Mark email as read
   * @param {String} emailId - Email ID
   * @returns {Promise<Object>} Update response
   */
  async markAsRead(emailId) {
    try {
      const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.EMAIL.DECRYPT}/${emailId}/read`);
      return response;
    } catch (error) {
      throw error;
    }
  }
};

export default emailAPI;

