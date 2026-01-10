package com.sakthivel.blockmail.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity(name = "blockchain_transactions")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class BlockchainTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String transactionHash;

    @Column(nullable = false)
    private String transactionType; // KEY_AUDIT, EMAIL_VERIFICATION

    @Column(nullable = false)
    private String dataHash; // Hash of the data stored on blockchain

    @Column
    private String referenceId; // ID of email or key being audited

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String blockchainNetwork; // HYPERLEDGER, ETHEREUM, etc.

    @Column
    private Long blockNumber;

    @Column(nullable = false)
    private Boolean verified = false;

    private LocalDateTime verifiedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON metadata
}

