package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingSchedule;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(
        name = "billing_schedule",
        uniqueConstraints = @UniqueConstraint(name = "uk_billing_policy_installment", columnNames = {"policy_id", "installment_no"})
)
public class BillingScheduleEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(name = "installment_no", nullable = false)
    private Integer installmentNo;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private BillingStatus status = BillingStatus.DUE;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public static BillingScheduleEntity toEntity(BillingSchedule billing) {
        BillingScheduleEntity entity = new BillingScheduleEntity();
        BeanUtils.copyProperties(billing, entity);
        return entity;
    }

    public BillingSchedule toDto() {
        BillingSchedule dto = new BillingSchedule();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);
        return dto;
    }
}
