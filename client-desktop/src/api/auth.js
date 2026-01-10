/**
 * Authentication API Service
 * Handles user authentication
 */

import apiClient from './client.js';
import { API_CONFIG } from './config.js';

export const authAPI = {
  /**
   * Register a new user
   * @param {Object} userData - User registration data (email, password)
   * @returns {Promise<Object>} User data and token
   */
  async register(userData) {
    try {
      await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.REGISTER, userData);
      // Backend returns success message, now login to get token
      return await this.login({ email: userData.email, password: userData.password });
    } catch (error) {
      throw error;
    }
  },

  /**
   * Login user
   * @param {Object} credentials - Login credentials (email, password)
   * @returns {Promise<Object>} User data and token
   */
  async login(credentials) {
    try {
      const token = await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.LOGIN, credentials);
      // Backend returns just the JWT token string
      if (token && typeof token === 'string') {
        apiClient.setAuthToken(token);
        // Return formatted response with token and user email
        return {
          token: token,
          user: {
            email: credentials.email
          }
        };
      }
      throw new Error('Invalid login response');
    } catch (error) {
      throw error;
    }
  },

  /**
   * Logout user
   * @returns {Promise<Object>} Logout response
   */
  async logout() {
    try {
      // Backend doesn't have a logout endpoint, just clear local token
      apiClient.clearAuthToken();
      return { success: true, message: 'Logged out successfully' };
    } catch (error) {
      apiClient.clearAuthToken();
      return { success: true, message: 'Logged out successfully' };
    }
  },

  /**
   * Verify authentication token
   * @returns {Promise<Object>} Verification response
   */
  async verifyToken() {
    try {
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.AUTH.VERIFY);
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Refresh authentication token
   * @returns {Promise<Object>} New token
   */
  async refreshToken() {
    try {
      const response = await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.REFRESH);
      if (response.token || response.access_token) {
        apiClient.setAuthToken(response.token || response.access_token);
      }
      return response;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get all users (admin function)
   * @returns {Promise<Object>} List of users
   */
  async getUserList() {
    try {
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.USER.LIST);
      return response;
    } catch (error) {
      throw error;
    }
  }
};

export default authAPI;

