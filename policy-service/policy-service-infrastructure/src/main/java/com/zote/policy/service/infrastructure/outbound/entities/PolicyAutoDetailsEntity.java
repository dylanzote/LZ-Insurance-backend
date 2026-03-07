package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.VehicleType;
import com.zote.policy.service.domain.models.PolicyAutoDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_auto_details")
public class PolicyAutoDetailsEntity extends Auditable {

    @Id
    @Column(name = "policy_version_id")
    private String policyVersionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "policy_version_id")
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 64)
    private String make;

    @Column(nullable = false, length = 64)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 24)
    private VehicleType vehicleType;

    @Column(length = 64)
    private String vin;

    @Column(name = "license_plate", length = 32)
    private String licensePlate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @OneToMany(mappedBy = "policyVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyAutoDriverEntity> drivers;

    public static PolicyAutoDetailsEntity toEntity(PolicyAutoDetails details) {
        PolicyAutoDetailsEntity entity = new PolicyAutoDetailsEntity();
        BeanUtils.copyProperties(details, entity);

        if (details.getDrivers() != null) {
            var drivers = details.getDrivers().stream()
                    .map(PolicyAutoDriverEntity::toEntity)
                    .toList();
            entity.setDrivers(new ArrayList<>(drivers));
        }
        return entity;
    }

    public PolicyAutoDetails toDto() {
        PolicyAutoDetails dto = new PolicyAutoDetails();
        BeanUtils.copyProperties(this, dto);
        if (this.drivers != null) {
            dto.setDrivers(this.drivers.stream().map(PolicyAutoDriverEntity::toDto).toList());
        }
        return dto;
    }
}
