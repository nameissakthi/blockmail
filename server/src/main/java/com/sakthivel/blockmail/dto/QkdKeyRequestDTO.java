package com.sakthivel.blockmail.dto;

import lombok.*;

/**
 * DTO for ETSI GS QKD 014 Key Request
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class QkdKeyRequestDTO {

    private Integer numberOfKeys = 1;  // Number of keys requested
    private Integer keySize;           // Requested key size in bits (optional)
    private String slaveKmId;          // Target KM ID for key sharing
}

