package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyVersionReason;
import com.zote.policy.service.domain.models.PolicyVersion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(
        name = "policy_version",
        uniqueConstraints = @UniqueConstraint(name = "uk_policy_version_policy_versionno", columnNames = {"policy_id", "version_no"})
)
public class PolicyVersionEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PolicyVersionReason reason;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "premium_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumTotal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> snapshot;

    @OneToMany(mappedBy = "policyVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyCoverageEntity> coverages;

    @OneToOne(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PolicyAutoDetailsEntity autoDetails;

    @OneToOne(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PolicyHomeDetailsEntity homeDetails;

    @OneToOne(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PolicyLifeDetailsEntity lifeDetails;

    @OneToOne(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PolicyHealthDetailsEntity healthDetails;

    @OneToOne(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PolicyTravelDetailsEntity travelDetails;

    public static PolicyVersionEntity toEntity(PolicyVersion version) {
        PolicyVersionEntity entity = new PolicyVersionEntity();
        BeanUtils.copyProperties(version, entity);

        if (version.getCoverages() != null) {
            entity.setCoverages(version.getCoverages().stream()
                    .map(c -> {
                        var ce = PolicyCoverageEntity.toEntity(c);
                        ce.setPolicyVersion(entity);
                        return ce;
                    })
                    .collect(Collectors.toList()));
        }

        if (version.getAutoDetails() != null) {
            var d = PolicyAutoDetailsEntity.toEntity(version.getAutoDetails());
            d.setPolicyVersion(entity);
            entity.setAutoDetails(d);
        }
        if (version.getHomeDetails() != null) {
            var d = PolicyHomeDetailsEntity.toEntity(version.getHomeDetails());
            d.setPolicyVersion(entity);
            entity.setHomeDetails(d);
        }
        if (version.getLifeDetails() != null) {
            var d = PolicyLifeDetailsEntity.toEntity(version.getLifeDetails());
            d.setPolicyVersion(entity);
            entity.setLifeDetails(d);
        }
        if (version.getHealthDetails() != null) {
            var d = PolicyHealthDetailsEntity.toEntity(version.getHealthDetails());
            d.setPolicyVersion(entity);
            entity.setHealthDetails(d);
        }
        if (version.getTravelDetails() != null) {
            var d = PolicyTravelDetailsEntity.toEntity(version.getTravelDetails());
            d.setPolicyVersion(entity);
            entity.setTravelDetails(d);
        }

        return entity;
    }

    public PolicyVersion toDto() {
        PolicyVersion dto = new PolicyVersion();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);

        dto.setCoverages(this.coverages != null
                ? this.coverages.stream().map(PolicyCoverageEntity::toDto).collect(Collectors.toList())
                : List.of());

        if (this.autoDetails != null) dto.setAutoDetails(this.autoDetails.toDto());
        if (this.homeDetails != null) dto.setHomeDetails(this.homeDetails.toDto());
        if (this.lifeDetails != null) dto.setLifeDetails(this.lifeDetails.toDto());
        if (this.healthDetails != null) dto.setHealthDetails(this.healthDetails.toDto());
        if (this.travelDetails != null) dto.setTravelDetails(this.travelDetails.toDto());

        return dto;
    }
}
