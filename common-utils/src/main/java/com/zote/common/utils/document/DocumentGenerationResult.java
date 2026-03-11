package com.zote.common.utils.document;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Result of document generation.
 * Caller stores documentUrl (or documentId) in their own database.
 */
@Data
@Builder
public class DocumentGenerationResult {
    private String documentId;
    private String documentUrl;
    private String documentType;
    private LocalDateTime createdAt;
}
