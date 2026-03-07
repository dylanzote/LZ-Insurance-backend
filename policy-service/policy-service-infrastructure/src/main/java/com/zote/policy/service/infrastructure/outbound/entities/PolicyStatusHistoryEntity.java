package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.PolicyStatusHistory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_status_history")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "changed_at", updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class PolicyStatusHistoryEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 24)
    private PolicyStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 24)
    private PolicyStatus toStatus;

    @Column(columnDefinition = "text")
    private String reason;

    @Column(name = "changed_by", length = 64)
    private String changedBy;

    public static PolicyStatusHistoryEntity toEntity(PolicyStatusHistory history) {
        PolicyStatusHistoryEntity entity = new PolicyStatusHistoryEntity();
        BeanUtils.copyProperties(history, entity);
        return entity;
    }

    public PolicyStatusHistory toDto() {
        PolicyStatusHistory dto = new PolicyStatusHistory();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);
        dto.setChangedAt(this.getCreatedAt());
        return dto;
    }
}
