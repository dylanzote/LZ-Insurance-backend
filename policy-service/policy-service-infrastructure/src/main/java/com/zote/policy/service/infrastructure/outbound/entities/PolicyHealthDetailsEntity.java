package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyHealthDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_health_details")
public class PolicyHealthDetailsEntity extends Auditable {

    @Id
    @Column(name = "policy_version_id")
    private String policyVersionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "policy_version_id")
    private PolicyVersionEntity policyVersion;

    @Column(name = "plan_code", nullable = false, length = 64)
    private String planCode;

    @Column(length = 24)
    private String tier;

    @Column(precision = 12, scale = 2)
    private BigDecimal deductible;

    @Column(precision = 12, scale = 2)
    private BigDecimal copay;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @OneToMany(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyHealthMemberEntity> members;

    public static PolicyHealthDetailsEntity toEntity(PolicyHealthDetails d) {
        PolicyHealthDetailsEntity entity = new PolicyHealthDetailsEntity();
        BeanUtils.copyProperties(d, entity);

        if (d.getMembers() != null) {
            entity.setMembers(new ArrayList<>(
                    d.getMembers().stream().map(PolicyHealthMemberEntity::toEntity).toList()
            ));
        }
        return entity;
    }

    public PolicyHealthDetails toDto() {
        PolicyHealthDetails dto = new PolicyHealthDetails();
        BeanUtils.copyProperties(this, dto);

        if (this.members != null) {
            dto.setMembers(this.members.stream().map(PolicyHealthMemberEntity::toDto).toList());
        }
        return dto;
    }
}
