package com.sakthivel.blockmail.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "encrypted_emails")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = {"encryptedContent", "attachments"})
public class EncryptedEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedContent; // Base64 encoded encrypted content

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SecurityLevel securityLevel;

    @Column
    private String quantumKeyId; // Reference to quantum key used

    @Column
    private String initializationVector; // IV for symmetric encryption

    @Lob
    @Column(columnDefinition = "TEXT")
    private String encryptionMetadata; // JSON metadata about encryption

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailAttachment> attachments = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column
    private String blockchainTxHash; // Reference to blockchain verification

    @Column(nullable = false)
    private Boolean isReceived = false;

    private LocalDateTime receivedAt;

    @Column
    private String messageHash; // SHA-256 hash for integrity verification

    @Column(nullable = false)
    private Boolean deletedBySender = false; // Sender deleted from their sent folder

    @Column(nullable = false)
    private Boolean deletedByRecipient = false; // Recipient deleted from their inbox
}

