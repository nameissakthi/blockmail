package com.sakthivel.blockmail.model;

public enum KeyStatus {
    RESERVED,   // Reserved from KM but not yet used
    ACTIVE,     // Ready for use
    USED,       // Has been used for encryption/decryption
    EXPIRED,    // Exceeded lifetime
    DESTROYED   // Securely deleted
}

