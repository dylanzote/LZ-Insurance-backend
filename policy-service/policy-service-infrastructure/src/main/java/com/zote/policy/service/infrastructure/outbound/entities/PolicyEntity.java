package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.SaleSource;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.Policy;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy")
public class PolicyEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "policy_number", nullable = false, unique = true, length = 64)
    private String policyNumber;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Column(name = "product_config_id", nullable = false, length = 64)
    private String productConfigId;

    @Column(name = "agent_id", length = 64)
    private String agentId;

    @Column(name = "branch_id", length = 64)
    private String branchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PolicyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_plan", nullable = false, length = 24)
    private BillingPlan billingPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PolicyType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 24)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "premium_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumTotal = BigDecimal.ZERO;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 24)
    private SaleSource source;

    @Column(name = "parent_policy_id", length = 64)
    private String parentPolicyId;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "timezone", length = 64)
    private String timezone;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyVersionEntity> versions;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EndorsementEntity> endorsements;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyDocumentEntity> documents;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingScheduleEntity> billingSchedules;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyStatusHistoryEntity> statusHistory;

    public static PolicyEntity toEntity(Policy policy) {
        PolicyEntity entity = new PolicyEntity();
        BeanUtils.copyProperties(policy, entity);

        // Children (only if provided)
        if (policy.getVersions() != null) {
            entity.setVersions(policy.getVersions().stream()
                    .map(v -> {
                        var ve = PolicyVersionEntity.toEntity(v);
                        ve.setPolicy(entity);
                        return ve;
                    })
                    .collect(Collectors.toList()));
        }

        if (policy.getEndorsements() != null) {
            entity.setEndorsements(policy.getEndorsements().stream()
                    .map(e -> {
                        var ee = EndorsementEntity.toEntity(e);
                        ee.setPolicy(entity);
                        return ee;
                    })
                    .collect(Collectors.toList()));
        }

        if (policy.getDocuments() != null) {
            entity.setDocuments(policy.getDocuments().stream()
                    .map(d -> {
                        var de = PolicyDocumentEntity.toEntity(d);
                        de.setPolicy(entity);
                        return de;
                    })
                    .collect(Collectors.toList()));
        }

        if (policy.getPayments() != null) {
            entity.setPayments(policy.getPayments().stream()
                    .map(p -> {
                        var pe = PaymentEntity.toEntity(p);
                        pe.setPolicy(entity);
                        return pe;
                    })
                    .collect(Collectors.toList()));
        }

        if (policy.getBillingSchedules() != null) {
            entity.setBillingSchedules(policy.getBillingSchedules().stream()
                    .map(b -> {
                        var be = BillingScheduleEntity.toEntity(b);
                        be.setPolicy(entity);
                        return be;
                    })
                    .collect(Collectors.toList()));
        }

        if (policy.getStatusHistory() != null) {
            entity.setStatusHistory(policy.getStatusHistory().stream()
                    .map(h -> {
                        var he = PolicyStatusHistoryEntity.toEntity(h);
                        he.setPolicy(entity);
                        return he;
                    })
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    public Policy toDto() {
        Policy dto = new Policy();
        BeanUtils.copyProperties(this, dto);

        dto.setVersions(this.versions != null
                ? this.versions.stream().map(PolicyVersionEntity::toDto).collect(Collectors.toList())
                : List.of());

        dto.setEndorsements(this.endorsements != null
                ? this.endorsements.stream().map(EndorsementEntity::toDto).collect(Collectors.toList())
                : List.of());

        dto.setDocuments(this.documents != null
                ? this.documents.stream().map(PolicyDocumentEntity::toDto).collect(Collectors.toList())
                : List.of());

        dto.setPayments(this.payments != null
                ? this.payments.stream().map(PaymentEntity::toDto).collect(Collectors.toList())
                : List.of());

        dto.setBillingSchedules(this.billingSchedules != null
                ? this.billingSchedules.stream().map(BillingScheduleEntity::toDto).collect(Collectors.toList())
                : List.of());

        dto.setStatusHistory(this.statusHistory != null
                ? this.statusHistory.stream().map(PolicyStatusHistoryEntity::toDto).collect(Collectors.toList())
                : List.of());

        return dto;
    }

}
