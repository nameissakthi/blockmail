package com.sakthivel.blockmail.model;

public enum SecurityLevel {
    QUANTUM_SECURE_OTP(1, "Quantum Secure - One Time Pad", "Perfect secrecy using quantum keys as OTP"),
    QUANTUM_AIDED_AES(2, "Quantum-aided AES", "AES encryption with quantum key as seed"),
    POST_QUANTUM_CRYPTO(3, "Post-Quantum Cryptography", "PQC algorithms like Kyber/Dilithium"),
    STANDARD_ENCRYPTION(4, "No Quantum Security", "Standard AES encryption");

    private final int level;
    private final String name;
    private final String description;

    SecurityLevel(int level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
