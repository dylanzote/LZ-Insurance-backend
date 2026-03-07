package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyHealthMember;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_health_member")
public class PolicyHealthMemberEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 24)
    private String relationship;

    private LocalDate dob;

    public static PolicyHealthMemberEntity toEntity(PolicyHealthMember m) {
        PolicyHealthMemberEntity entity = new PolicyHealthMemberEntity();
        BeanUtils.copyProperties(m, entity);
        return entity;
    }

    public PolicyHealthMember toDto() {
        PolicyHealthMember dto = new PolicyHealthMember();
        BeanUtils.copyProperties(this, dto);
        if (this.policyVersion != null) dto.setPolicyVersionId(this.policyVersion.getId());
        return dto;
    }
}
