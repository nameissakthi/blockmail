package com.sakthivel.blockmail.dto;

import lombok.*;

/**
 * DTO for ETSI GS QKD 014 Key Material Response
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "key")
public class QkdKeyMaterialDTO {

    private String keyId;           // Unique key identifier from KM
    private String key;             // Base64 encoded key material
    private Integer keySize;        // Key size in bits
    private String status;          // Key status from KM
    private Long timestamp;         // Unix timestamp
    private String sourceKmId;      // Source Key Manager ID
    private String destinationKmId; // Destination Key Manager ID (for key sharing)
}
