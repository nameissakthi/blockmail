package com.sakthivel.blockmail.dto;

import lombok.*;

/**
 * DTO for email attachments
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class AttachmentDTO {

    private String fileName;
    private String contentType;
    private String base64Data; // Base64 encoded file content
    private Long fileSize;
}

