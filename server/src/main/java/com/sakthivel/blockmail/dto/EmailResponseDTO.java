package com.sakthivel.blockmail.dto;

import com.sakthivel.blockmail.model.SecurityLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for email response
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class EmailResponseDTO {

    private String id;
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String content; // Decrypted content
    private SecurityLevel securityLevel;
    private LocalDateTime sentAt;
    private String blockchainTxHash;
    private Boolean verified;
    private List<AttachmentResponseDTO> attachments;
}

