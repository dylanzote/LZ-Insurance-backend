package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.policy.service.domain.models.PolicyAuditLog;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "policy_audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAuditLogEntity {

    @Id
    private String id;

    @Column(name = "policy_id", nullable = false, length = 64)
    private String policyId;

    @Column(name = "change_type", nullable = false, length = 64)
    private String changeType;

    @Column(name = "entity_type", length = 64)
    private String entityType;

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private Map<String, Object> oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private Map<String, Object> newValue;

    @Column(name = "changed_by", length = 64)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    public static PolicyAuditLogEntity from(PolicyAuditLog log) {
        return PolicyAuditLogEntity.builder()
                .id(log.getId())
                .policyId(log.getPolicyId())
                .changeType(log.getChangeType())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .changedBy(log.getChangedBy())
                .changedAt(log.getChangedAt())
                .build();
    }

    public PolicyAuditLog toDto() {
        return PolicyAuditLog.builder()
                .id(id)
                .policyId(policyId)
                .changeType(changeType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .changedAt(changedAt)
                .build();
    }
}
