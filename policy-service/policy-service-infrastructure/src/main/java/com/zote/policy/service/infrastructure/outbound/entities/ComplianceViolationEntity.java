package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.policy.service.domain.models.ComplianceViolation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "compliance_violation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceViolationEntity {

    @Id
    private String id;

    @Column(name = "policy_id", nullable = false, length = 64)
    private String policyId;

    @Column(name = "violation_type", nullable = false, length = 64)
    private String violationType;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "severity", nullable = false, length = 24)
    private String severity;

    @Column(name = "status", nullable = false, length = 24)
    private String status;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", length = 64)
    private String resolvedBy;

    @Column(name = "resolution_notes", columnDefinition = "text")
    private String resolutionNotes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    public static ComplianceViolationEntity from(ComplianceViolation v) {
        return ComplianceViolationEntity.builder()
                .id(v.getId())
                .policyId(v.getPolicyId())
                .violationType(v.getViolationType())
                .description(v.getDescription())
                .severity(v.getSeverity())
                .status(v.getStatus())
                .detectedAt(v.getDetectedAt())
                .resolvedAt(v.getResolvedAt())
                .resolvedBy(v.getResolvedBy())
                .resolutionNotes(v.getResolutionNotes())
                .metadata(v.getMetadata())
                .build();
    }

    public ComplianceViolation toDto() {
        return ComplianceViolation.builder()
                .id(id)
                .policyId(policyId)
                .violationType(violationType)
                .description(description)
                .severity(severity)
                .status(status)
                .detectedAt(detectedAt)
                .resolvedAt(resolvedAt)
                .resolvedBy(resolvedBy)
                .resolutionNotes(resolutionNotes)
                .metadata(metadata)
                .build();
    }
}
