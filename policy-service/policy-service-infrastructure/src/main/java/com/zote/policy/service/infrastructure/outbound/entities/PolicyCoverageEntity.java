package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyCoverage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_coverage")
public class PolicyCoverageEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(name = "coverage_code", nullable = false, length = 64)
    private String coverageCode;

    @Column(name = "limit_amount", precision = 12, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "deductible_amount", precision = 12, scale = 2)
    private BigDecimal deductibleAmount;

    @Column(name = "premium_portion", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumPortion = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    public static PolicyCoverageEntity toEntity(PolicyCoverage coverage) {
        PolicyCoverageEntity entity = new PolicyCoverageEntity();
        BeanUtils.copyProperties(coverage, entity);
        return entity;
    }

    public PolicyCoverage toDto() {
        PolicyCoverage dto = new PolicyCoverage();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyVersionId(this.policyVersion != null ? this.policyVersion.getId() : null);
        return dto;
    }
}
