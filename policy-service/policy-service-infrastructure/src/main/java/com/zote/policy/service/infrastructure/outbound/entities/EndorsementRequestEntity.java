package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.EndorsementRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "endorsement_request")
public class EndorsementRequestEntity {

    @Id
    private String id;

    @Column(name = "policy_id", nullable = false)
    private String policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EndorsementType type;

    @Column(columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> changes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EndorsementRequestStatus status;

    @Column(name = "requested_by", length = 64)
    private String requestedBy;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "decided_by", length = 64)
    private String decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "rejection_reason", length = 1024)
    private String rejectionReason;

    @Column(name = "endorsement_id", length = 64)
    private String endorsementId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static EndorsementRequestEntity toEntity(EndorsementRequest request) {
        return EndorsementRequestEntity.builder()
                .id(request.getId())
                .policyId(request.getPolicyId())
                .type(request.getType())
                .description(request.getDescription())
                .changes(request.getChanges())
                .status(request.getStatus())
                .requestedBy(request.getRequestedBy())
                .requestedAt(request.getRequestedAt())
                .decidedBy(request.getDecidedBy())
                .decidedAt(request.getDecidedAt())
                .rejectionReason(request.getRejectionReason())
                .endorsementId(request.getEndorsementId())
                .createdAt(request.getRequestedAt() != null ? request.getRequestedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public EndorsementRequest toDto() {
        return EndorsementRequest.builder()
                .id(id)
                .policyId(policyId)
                .type(type)
                .description(description)
                .changes(changes)
                .status(status)
                .requestedBy(requestedBy)
                .requestedAt(requestedAt)
                .decidedBy(decidedBy)
                .decidedAt(decidedAt)
                .rejectionReason(rejectionReason)
                .endorsementId(endorsementId)
                .build();
    }
}
