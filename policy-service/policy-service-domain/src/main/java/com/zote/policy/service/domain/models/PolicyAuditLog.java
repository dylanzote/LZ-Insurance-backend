package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Immutable audit record for policy changes (14.1, 14.6).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAuditLog {
    private String id;
    private String policyId;
    private String changeType;
    private String entityType;
    private String entityId;
    private String description;
    private Map<String, Object> oldValue;
    private Map<String, Object> newValue;
    private String changedBy;
    private LocalDateTime changedAt;
}
