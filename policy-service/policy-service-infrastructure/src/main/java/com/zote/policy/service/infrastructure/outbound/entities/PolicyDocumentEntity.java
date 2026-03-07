package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_document")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "uploaded_at", updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class PolicyDocumentEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PolicyDocumentType type;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PolicyDocumentStatus status = PolicyDocumentStatus.PENDING;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by", length = 64)
    private String verifiedBy;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    public static PolicyDocumentEntity toEntity(PolicyDocument doc) {
        PolicyDocumentEntity entity = new PolicyDocumentEntity();
        BeanUtils.copyProperties(doc, entity);
        return entity;
    }

    public PolicyDocument toDto() {
        PolicyDocument dto = new PolicyDocument();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);
        dto.setUploadedAt(this.getCreatedAt());
        return dto;
    }
}
