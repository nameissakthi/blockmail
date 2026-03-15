/**
 * API Client
 * Handles HTTP requests to the backend
 */

import axios from 'axios';
import { API_CONFIG } from './config.js';

class ApiClient {
  constructor() {
    this.client = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = this.getAuthToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        if (error.response?.status === 401) {
          // Token expired, try to refresh
          await this.handleTokenExpiry();
        }
        return Promise.reject(error);
      }
    );
  }

  getAuthToken() {
    return localStorage.getItem('authToken');
  }

  setAuthToken(token) {
    localStorage.setItem('authToken', token);
  }

  clearAuthToken() {
    localStorage.removeItem('authToken');
  }

  async handleTokenExpiry() {
    // Clear token and redirect to login
    this.clearAuthToken();
    window.dispatchEvent(new CustomEvent('auth:logout'));
  }

  async get(url, config = {}) {
    try {
      const response = await this.client.get(url, config);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async post(url, data = {}, config = {}) {
    try {
      const response = await this.client.post(url, data, config);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async put(url, data = {}, config = {}) {
    try {
      const response = await this.client.put(url, data, config);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async delete(url, config = {}) {
    try {
      const response = await this.client.delete(url, config);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  handleError(error) {
    if (error.code === 'ECONNABORTED') {
      return {
        message: 'Request timed out. The server took too long to respond.',
        status: 408
      };
    }

    if (error.response) {
      // Server responded with error status
      const responseData = error.response.data;
      const message =
        typeof responseData === 'string'
          ? responseData
          : responseData?.message || responseData?.error || error.message || 'Server error occurred';

      return {
        message,
        status: error.response.status,
        data: responseData
      };
    } else if (error.request) {
      // Request made but no response
      return {
        message: 'No response from server. Please check your connection.',
        status: 0
      };
    } else {
      // Error in request setup
      return {
        message: error.message || 'An error occurred',
        status: -1
      };
    }
  }
}

export default new ApiClient();

