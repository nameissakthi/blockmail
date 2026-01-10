package com.sakthivel.blockmail.dto;

import lombok.*;

/**
 * DTO for blockchain verification response
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class BlockchainVerificationDTO {

    private String transactionHash;
    private String referenceId;
    private String dataHash;
    private Boolean verified;
    private Long blockNumber;
    private String network;
    private String timestamp;
}

