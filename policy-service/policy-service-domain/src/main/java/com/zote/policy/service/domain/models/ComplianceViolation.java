package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Detected compliance violation (14.3).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceViolation {
    private String id;
    private String policyId;
    private String violationType;
    private String description;
    private String severity;
    private String status;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Map<String, Object> metadata;
}
