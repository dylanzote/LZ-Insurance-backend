package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.LifePolicyForm;
import com.zote.policy.service.domain.models.PolicyLifeDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_life_details")
public class PolicyLifeDetailsEntity extends Auditable {

    @Id
    @Column(name = "policy_version_id")
    private String policyVersionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "policy_version_id")
    private PolicyVersionEntity policyVersion;

    @Column(name = "insured_name", nullable = false, length = 128)
    private String insuredName;

    @Column(name = "insured_dob")
    private LocalDate insuredDob;

    @Column(name = "sum_assured", nullable = false, precision = 12, scale = 2)
    private BigDecimal sumAssured;

    @Column(name = "term_years")
    private Integer termYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_type", nullable = false, length = 24)
    private LifePolicyForm lifeType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @OneToMany(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyLifeBeneficiaryEntity> beneficiaries;

    public static PolicyLifeDetailsEntity toEntity(PolicyLifeDetails d) {
        PolicyLifeDetailsEntity entity = new PolicyLifeDetailsEntity();
        BeanUtils.copyProperties(d, entity);

        if (d.getBeneficiaries() != null) {
            entity.setBeneficiaries(new ArrayList<>(
                    d.getBeneficiaries().stream().map(PolicyLifeBeneficiaryEntity::toEntity).toList()
            ));
        }
        return entity;
    }

    public PolicyLifeDetails toDto() {
        PolicyLifeDetails dto = new PolicyLifeDetails();
        BeanUtils.copyProperties(this, dto);
        if (this.beneficiaries != null) {
            dto.setBeneficiaries(this.beneficiaries.stream().map(PolicyLifeBeneficiaryEntity::toDto).toList());
        }
        return dto;
    }
}
