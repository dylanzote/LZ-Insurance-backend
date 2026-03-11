package com.zote.common.utils.document;

import lombok.Builder;
import lombok.Data;

/**
 * Optional metadata for document generation.
 */
@Data
@Builder
public class DocumentMetadata {
    private String documentType;
    private String version;
    private String language;
    /** Service name (e.g. policy-service, claims-service) for object storage path */
    private String serviceName;
    /** Entity ID (e.g. policy ID, claim ID) for object storage path */
    private String entityId;
    /** Optional display name for the file */
    private String fileName;
}
