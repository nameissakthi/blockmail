package com.sakthivel.blockmail.dto;

import com.sakthivel.blockmail.model.SecurityLevel;
import lombok.*;

import java.util.List;

/**
 * DTO for sending secure email
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class SendEmailRequestDTO {

    private String recipientEmail;
    private String subject;
    private String content;
    private SecurityLevel securityLevel = SecurityLevel.QUANTUM_AIDED_AES;
    private List<AttachmentDTO> attachments;
}

