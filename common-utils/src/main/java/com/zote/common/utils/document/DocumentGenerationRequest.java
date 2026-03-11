package com.zote.common.utils.document;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request for generic document generation.
 * Provide either htmlTemplate (inline) or templatePath (classpath resource).
 */
@Data
@Builder
public class DocumentGenerationRequest {
    /** Inline HTML template with {{variableName}} placeholders */
    private String htmlTemplate;
    /** Classpath path to HTML template (e.g. templates/documents/policy-schedule.html) */
    private String templatePath;
    /** Data to merge into template */
    private Map<String, Object> data;
    /** Optional metadata */
    private DocumentMetadata metadata;
    /** Output format - currently only PDF supported */
    @Builder.Default
    private String outputFormat = "pdf";
}
