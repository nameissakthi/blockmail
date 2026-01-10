package com.sakthivel.blockmail.dto;

import lombok.*;

/**
 * DTO for attachment response
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class AttachmentResponseDTO {

    private String id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String base64Data; // Decrypted file content
}

