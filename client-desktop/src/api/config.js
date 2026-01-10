/**
 * API Configuration
 * Configure the base URLs for backend services
 * Uses environment variables for security
 */

// Get base URL from environment variable or use default
const getBaseUrl = () => {
  // In Vite, environment variables are accessed via import.meta.env
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
};

export const API_CONFIG = {
  // Backend API base URL - Configured via .env file
  BASE_URL: getBaseUrl(),

  // Timeout for API requests (ms)
  TIMEOUT: 30000,

  // API Endpoints - Updated to match your backend
  ENDPOINTS: {
    // Authentication (no /api prefix)
    AUTH: {
      REGISTER: '/register',
      LOGIN: '/login',
      LOGOUT: '/logout',
      VERIFY: '/auth/verify',
      REFRESH: '/auth/refresh'
    },

    // User Management
    USER: {
      LIST: '/user/list',
      PROFILE: '/user/profile',
      SETTINGS: '/user/settings',
      UPDATE: '/user/update'
    },

    // Quantum Key Distribution (QKD) - with /api prefix
    QKD: {
      OBTAIN_KEYS: '/api/qkd/obtain-keys',
      KEY_STATUS: '/api/qkd/key-status',
      ACTIVATE_KEY: '/api/qkd/activate-key',
      DESTROY_KEY: '/api/qkd/destroy-key'
    },

    // Quantum Email Operations - with /api prefix
    EMAIL: {
      SEND: '/api/quantum-email/send',
      SENT: '/api/quantum-email/sent',
      RECEIVED: '/api/quantum-email/received',
      DECRYPT: '/api/quantum-email/decrypt'
    },

    // Blockchain Verification - with /api prefix
    BLOCKCHAIN: {
      VERIFY: '/api/blockchain/verify',
      TRANSACTION: '/api/blockchain/transaction'
    }
  }
};

export default API_CONFIG;


