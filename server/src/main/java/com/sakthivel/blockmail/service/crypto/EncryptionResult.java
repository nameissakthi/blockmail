package com.sakthivel.blockmail.service.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Result of encryption operation
 */
@Getter @Setter
@AllArgsConstructor
@ToString(exclude = "ciphertext")
public class EncryptionResult {

    private byte[] ciphertext;
    private Map<String, String> metadata;

    public EncryptionResult() {
        this.metadata = new HashMap<>();
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
}

