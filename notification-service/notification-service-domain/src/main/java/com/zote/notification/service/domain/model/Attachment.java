package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    private String filename;
    private String contentType;
    private byte[] content;
    private String url; // Or base64 encoded content
    private boolean inline;
}
