package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.Endorsement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "endorsement")
public class EndorsementEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EndorsementType type;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "premium_change", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumChange = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> changes;

    public static EndorsementEntity toEntity(Endorsement endorsement) {
        EndorsementEntity entity = new EndorsementEntity();
        BeanUtils.copyProperties(endorsement, entity);
        return entity;
    }

    public Endorsement toDto() {
        Endorsement dto = new Endorsement();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);
        return dto;
    }
}
