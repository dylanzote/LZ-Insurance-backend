package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PaymentMethod;
import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.domain.models.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "payment")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "recorded_at", updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class PaymentEntity extends Auditable {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PaymentMethod method;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "transaction_id", length = 128)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PaymentRecordStatus status = PaymentRecordStatus.RECORDED;

    @Column(name = "failure_reason", length = 512)
    private String failureReason;

    public static PaymentEntity toEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        BeanUtils.copyProperties(payment, entity);
        return entity;
    }

    public Payment toDto() {
        Payment dto = new Payment();
        BeanUtils.copyProperties(this, dto);
        dto.setPolicyId(this.policy != null ? this.policy.getId() : null);
        dto.setRecordedAt(this.getCreatedAt());
        return dto;
    }
}
