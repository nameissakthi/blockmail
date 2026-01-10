package com.sakthivel.blockmail.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "email_attachments")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "encryptedData")
public class EmailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", nullable = false)
    private EncryptedEmail email;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize; // Size in bytes

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedData; // Base64 encoded encrypted attachment

    @Column
    private String quantumKeyId; // Separate quantum key for attachment

    @Column
    private String initializationVector;

    @Column
    private String fileHash; // SHA-256 hash of original file for integrity
}

