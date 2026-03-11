package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_product_config")
public class PolicyProductConfigEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "product_id", nullable = false, unique = true, length = 64)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 24)
    private PolicyType policyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_unit", nullable = false, length = 16)
    private PolicyTermUnit termUnit;

    @Column(name = "term_length")
    private Integer termLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_billing_plan", nullable = false, length = 24)
    private BillingPlan defaultBillingPlan;

    @Column(name = "allow_full_payment", nullable = false)
    private boolean allowFullPayment;

    @Column(name = "allow_installments", nullable = false)
    private boolean allowInstallments;

    @Column(name = "installments_count")
    private Integer installmentsCount;

    @Column(name = "require_documents", nullable = false)
    private boolean requireDocuments;

    @Column(name = "requires_underwriting", nullable = false)
    private boolean requiresUnderwriting;

    @Column(name = "max_premium", precision = 19, scale = 4)
    private BigDecimal maxPremium;

    @Column(name = "min_premium", precision = 19, scale = 4)
    private BigDecimal minPremium;

    @Column(name = "max_grace_period_days")
    private Integer maxGracePeriodDays;

    public static PolicyProductConfigEntity toEntity(PolicyProductConfig model) {
        PolicyProductConfigEntity entity = new PolicyProductConfigEntity();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public PolicyProductConfig toDto() {
        PolicyProductConfig dto = new PolicyProductConfig();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
