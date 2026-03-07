package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyTravelDetails;
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
@Table(name = "policy_travel_details")
public class PolicyTravelDetailsEntity extends Auditable {

    @Id
    @Column(name = "policy_version_id")
    private String policyVersionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "policy_version_id")
    private PolicyVersionEntity policyVersion;

    @Column(name = "trip_start", nullable = false)
    private LocalDate tripStart;

    @Column(name = "trip_end", nullable = false)
    private LocalDate tripEnd;

    @Column(nullable = false, length = 128)
    private String destination;

    @Column(name = "trip_cost", precision = 12, scale = 2)
    private BigDecimal tripCost;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @OneToMany(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyTravelTravellerEntity> travellers;

    public static PolicyTravelDetailsEntity toEntity(PolicyTravelDetails d) {
        PolicyTravelDetailsEntity entity = new PolicyTravelDetailsEntity();
        BeanUtils.copyProperties(d, entity);

        if (d.getTravellers() != null) {
            entity.setTravellers(new ArrayList<>(
                    d.getTravellers().stream().map(PolicyTravelTravellerEntity::toEntity).toList()
            ));
        }
        return entity;
    }

    public PolicyTravelDetails toDto() {
        PolicyTravelDetails dto = new PolicyTravelDetails();
        BeanUtils.copyProperties(this, dto);

        if (this.travellers != null) {
            dto.setTravellers(this.travellers.stream().map(PolicyTravelTravellerEntity::toDto).toList());
        }
        return dto;
    }
}
