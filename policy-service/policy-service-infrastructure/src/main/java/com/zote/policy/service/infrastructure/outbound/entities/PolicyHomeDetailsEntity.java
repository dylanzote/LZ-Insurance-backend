package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.PolicyHomeDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_home_details")
public class PolicyHomeDetailsEntity extends Auditable {

    @Id
    @Column(name = "policy_version_id")
    private String policyVersionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "policy_version_id")
    private PolicyVersionEntity policyVersion;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 64)
    private String city;

    @Column(length = 64)
    private String state;

    @Column(name = "zip_code", length = 16)
    private String zipCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    public static PolicyHomeDetailsEntity toEntity(PolicyHomeDetails d) {
        PolicyHomeDetailsEntity entity = new PolicyHomeDetailsEntity();
        BeanUtils.copyProperties(d, entity);
        return entity;
    }

    public PolicyHomeDetails toDto() {
        PolicyHomeDetails dto = new PolicyHomeDetails();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
