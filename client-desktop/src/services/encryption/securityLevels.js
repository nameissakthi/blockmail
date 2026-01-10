/**
 * Security Levels Configuration
 */

export const SECURITY_LEVELS = {
  QUANTUM_SECURE: {
    id: 1,
    name: 'Quantum Secure',
    description: 'One Time Pad (OTP) - Unconditional security using quantum keys',
    icon: '🔐',
    color: '#4CAF50'
  },
  QUANTUM_AES: {
    id: 2,
    name: 'Quantum-aided AES',
    description: 'AES-256 with quantum key seeding',
    icon: '🛡️',
    color: '#2196F3'
  },
  PQC: {
    id: 3,
    name: 'Post-Quantum Cryptography',
    description: 'PQC algorithms (e.g., Kyber, Dilithium)',
    icon: '🔒',
    color: '#FF9800'
  },
  STANDARD: {
    id: 4,
    name: 'Standard',
    description: 'Standard encryption without quantum security',
    icon: '🔓',
    color: '#9E9E9E'
  }
};

/**
 * Get security level by ID
 */
export function getSecurityLevel(id) {
  return Object.values(SECURITY_LEVELS).find(level => level.id === id) || SECURITY_LEVELS.STANDARD;
}

export default SECURITY_LEVELS;

