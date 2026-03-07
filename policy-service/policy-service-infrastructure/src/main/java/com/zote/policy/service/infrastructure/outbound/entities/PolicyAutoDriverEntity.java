package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.DriverType;
import com.zote.policy.service.domain.models.PolicyAutoDriver;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_auto_driver")
public class PolicyAutoDriverEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_type", nullable = false, length = 24)
    private DriverType driverType;

    public static PolicyAutoDriverEntity toEntity(PolicyAutoDriver d) {
        PolicyAutoDriverEntity entity = new PolicyAutoDriverEntity();
        BeanUtils.copyProperties(d, entity);
        return entity;
    }

    public PolicyAutoDriver toDto() {
        PolicyAutoDriver dto = new PolicyAutoDriver();
        BeanUtils.copyProperties(this, dto);
        if (this.policyVersion != null) dto.setPolicyVersionId(this.policyVersion.getId());
        return dto;
    }
}
