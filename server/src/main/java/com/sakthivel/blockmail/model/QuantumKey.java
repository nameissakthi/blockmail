package com.sakthivel.blockmail.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity(name = "quantum_keys")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "keyMaterial") // Don't log sensitive key material
public class QuantumKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String keyId; // Key ID from KM (ETSI QKD 014)

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String keyMaterial; // Base64 encoded quantum key

    @Column(nullable = false)
    private Integer keySize; // Size in bits

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status = KeyStatus.RESERVED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private LocalDateTime obtainedAt = LocalDateTime.now();

    private LocalDateTime usedAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private String kmSourceId; // Key Manager source identifier

    @Column
    private String recipientId; // For key sharing with specific recipient

    @Column
    private String blockchainTxHash; // Reference to blockchain audit record
}

