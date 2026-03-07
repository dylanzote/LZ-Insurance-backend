package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyTravelTraveller;
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
@Table(name = "policy_travel_traveller")
public class PolicyTravelTravellerEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 128)
    private String name;

    private LocalDate dob;

    public static PolicyTravelTravellerEntity toEntity(PolicyTravelTraveller t) {
        PolicyTravelTravellerEntity entity = new PolicyTravelTravellerEntity();
        BeanUtils.copyProperties(t, entity);
        return entity;
    }

    public PolicyTravelTraveller toDto() {
        PolicyTravelTraveller dto = new PolicyTravelTraveller();
        BeanUtils.copyProperties(this, dto);
        if (this.policyVersion != null) dto.setPolicyVersionId(this.policyVersion.getId());
        return dto;
    }
}
