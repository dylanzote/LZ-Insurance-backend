package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyLifeBeneficiary;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_life_beneficiary")
public class PolicyLifeBeneficiaryEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    public static PolicyLifeBeneficiaryEntity toEntity(PolicyLifeBeneficiary b) {
        PolicyLifeBeneficiaryEntity entity = new PolicyLifeBeneficiaryEntity();
        BeanUtils.copyProperties(b, entity);
        return entity;
    }

    public PolicyLifeBeneficiary toDto() {
        PolicyLifeBeneficiary dto = new PolicyLifeBeneficiary();
        BeanUtils.copyProperties(this, dto);
        if (this.policyVersion != null) dto.setPolicyVersionId(this.policyVersion.getId());
        return dto;
    }
}
