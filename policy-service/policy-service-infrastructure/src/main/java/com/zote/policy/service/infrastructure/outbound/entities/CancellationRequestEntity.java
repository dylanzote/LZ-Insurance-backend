package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.domain.enums.CancellationType;
import com.zote.policy.service.domain.models.CancellationRequest;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "cancellation_request")
public class CancellationRequestEntity {

    @Id
    private String id;

    @Column(name = "policy_id", nullable = false)
    private String policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_type", nullable = false, length = 32)
    private CancellationType cancellationType;

    @Column(columnDefinition = "text")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CancellationRequestStatus status;

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

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "refund_amount", precision = 19, scale = 4)
    private BigDecimal refundAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static CancellationRequestEntity toEntity(CancellationRequest request) {
        return CancellationRequestEntity.builder()
                .id(request.getId())
                .policyId(request.getPolicyId())
                .cancellationType(request.getCancellationType())
                .reason(request.getReason())
                .status(request.getStatus())
                .requestedBy(request.getRequestedBy())
                .requestedAt(request.getRequestedAt())
                .decidedBy(request.getDecidedBy())
                .decidedAt(request.getDecidedAt())
                .rejectionReason(request.getRejectionReason())
                .effectiveDate(request.getEffectiveDate())
                .refundAmount(request.getRefundAmount())
                .createdAt(request.getRequestedAt() != null ? request.getRequestedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CancellationRequest toDto() {
        return CancellationRequest.builder()
                .id(id)
                .policyId(policyId)
                .cancellationType(cancellationType)
                .reason(reason)
                .status(status)
                .requestedBy(requestedBy)
                .requestedAt(requestedAt)
                .decidedBy(decidedBy)
                .decidedAt(decidedAt)
                .rejectionReason(rejectionReason)
                .effectiveDate(effectiveDate)
                .refundAmount(refundAmount)
                .build();
    }
}
