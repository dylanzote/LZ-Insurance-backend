package com.zote.policy.service.api.response;

import com.zote.policy.service.api.support.SensitiveDataMasker;
import com.zote.policy.service.domain.models.PolicyAuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Audit log entry with masked sensitive data (14.5).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAuditLogResponse {

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

    public static PolicyAuditLogResponse from(PolicyAuditLog log) {
        return from(log, true);
    }

    public static PolicyAuditLogResponse from(PolicyAuditLog log, boolean maskSensitive) {
        return PolicyAuditLogResponse.builder()
                .id(log.getId())
                .policyId(log.getPolicyId())
                .changeType(log.getChangeType())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .oldValue(maskSensitive ? SensitiveDataMasker.maskSensitiveInMap(log.getOldValue()) : log.getOldValue())
                .newValue(maskSensitive ? SensitiveDataMasker.maskSensitiveInMap(log.getNewValue()) : log.getNewValue())
                .changedBy(log.getChangedBy())
                .changedAt(log.getChangedAt())
                .build();
    }
}
