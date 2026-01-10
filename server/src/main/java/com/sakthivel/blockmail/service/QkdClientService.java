package com.sakthivel.blockmail.service;

import com.sakthivel.blockmail.config.QkdKeyManagerConfig;
import com.sakthivel.blockmail.dto.QkdKeyMaterialDTO;
import com.sakthivel.blockmail.dto.QkdKeyRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * QKD Client Service implementing ETSI GS QKD 014 protocol
 * Handles communication with Key Manager for quantum key retrieval
 */
@Service
@Slf4j
public class QkdClientService {

    private final QkdKeyManagerConfig config;
    private final RestTemplate restTemplate;
    private final SecureRandom secureRandom;

    public QkdClientService(@Autowired QkdKeyManagerConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Get quantum keys from Key Manager (ETSI GS QKD 014 get_key endpoint)
     */
    public List<QkdKeyMaterialDTO> getQuantumKeys(QkdKeyRequestDTO request) {
        if (config.getMockMode()) {
            return getMockQuantumKeys(request);
        }

        try {
            String url = config.getBaseUrl() + "/get_key";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (config.getAuthToken() != null) {
                headers.setBearerAuth(config.getAuthToken());
            }

            HttpEntity<QkdKeyRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<QkdKeyMaterialDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                QkdKeyMaterialDTO[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Retrieved {} quantum keys from KM", response.getBody().length);
                return List.of(response.getBody());
            } else {
                throw new RuntimeException("Failed to retrieve quantum keys from KM");
            }
        } catch (Exception e) {
            log.error("Error communicating with Key Manager: {}", e.getMessage());
            throw new RuntimeException("QKD Key Manager communication error: " + e.getMessage(), e);
        }
    }

    /**
     * Get key status from Key Manager (ETSI GS QKD 014 get_status endpoint)
     */
    public String getKeyStatus(String keyId) {
        if (config.getMockMode()) {
            return "ACTIVE";
        }

        try {
            String url = config.getBaseUrl() + "/get_status/" + keyId;

            HttpHeaders headers = new HttpHeaders();
            if (config.getAuthToken() != null) {
                headers.setBearerAuth(config.getAuthToken());
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting key status: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * Mock quantum key generation for testing
     * Simulates ETSI GS QKD 014 response format
     */
    private List<QkdKeyMaterialDTO> getMockQuantumKeys(QkdKeyRequestDTO request) {
        log.info("Generating {} mock quantum keys (size: {} bits)",
                 request.getNumberOfKeys(),
                 request.getKeySize() != null ? request.getKeySize() : config.getDefaultKeySize());

        List<QkdKeyMaterialDTO> keys = new ArrayList<>();
        int keySize = request.getKeySize() != null ? request.getKeySize() : config.getDefaultKeySize();

        for (int i = 0; i < request.getNumberOfKeys(); i++) {
            // Generate random key material
            byte[] keyBytes = new byte[keySize / 8];
            secureRandom.nextBytes(keyBytes);
            String keyMaterial = Base64.getEncoder().encodeToString(keyBytes);

            QkdKeyMaterialDTO keyDTO = new QkdKeyMaterialDTO();
            keyDTO.setKeyId("QK_" + UUID.randomUUID().toString());
            keyDTO.setKey(keyMaterial);
            keyDTO.setKeySize(keySize);
            keyDTO.setStatus("ACTIVE");
            keyDTO.setTimestamp(System.currentTimeMillis());
            keyDTO.setSourceKmId(config.getMasterKmId());
            keyDTO.setDestinationKmId(request.getSlaveKmId());

            keys.add(keyDTO);
        }

        return keys;
    }

    /**
     * Request shared key with specific recipient KM
     */
    public QkdKeyMaterialDTO getSharedKey(String recipientKmId, Integer keySize) {
        QkdKeyRequestDTO request = new QkdKeyRequestDTO();
        request.setNumberOfKeys(1);
        request.setKeySize(keySize != null ? keySize : config.getDefaultKeySize());
        request.setSlaveKmId(recipientKmId);

        List<QkdKeyMaterialDTO> keys = getQuantumKeys(request);
        return keys.isEmpty() ? null : keys.get(0);
    }
}

