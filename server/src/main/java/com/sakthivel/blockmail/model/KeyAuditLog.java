package com.sakthivel.blockmail.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity(name = "key_audit_logs")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class KeyAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String quantumKeyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String action; // OBTAINED, ACTIVATED, USED, EXPIRED, DESTROYED

    @Column
    private String emailId; // Reference to email if key was used for email

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column
    private String blockchainTxHash; // Reference to blockchain transaction

    @Lob
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON metadata about the operation

    @Column(nullable = false)
    private Boolean blockchainVerified = false;
}

