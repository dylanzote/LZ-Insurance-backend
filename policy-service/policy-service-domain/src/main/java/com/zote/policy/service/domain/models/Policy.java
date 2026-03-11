package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.SaleSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Policy {
    private String id;
    private String policyNumber;
    private String productId;
    private String customerId;
    private String agentId;
    private String branchId;
    private String productConfigId;
    private PolicyStatus status;
    private PolicyType type;
    private PaymentStatus paymentStatus;
    private BillingPlan billingPlan;
    private String currency;
    private BigDecimal premiumTotal;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;

    /** Sales channel (web, mobile, agent, API). */
    private SaleSource source;

    /** Parent policy ID when this is a renewal. */
    private String parentPolicyId;

    /** Grace period in days before suspend/cancel for non-payment (nullable = use product default). */
    private Integer gracePeriodDays;

    /** When the policy was issued (may differ from createdAt). */
    private LocalDateTime issuedAt;

    /** Timezone for effective/expiry dates (e.g. Africa/Douala). */
    private String timezone;
    private List<PolicyVersion> versions;
    private List<Endorsement> endorsements;
    private List<PolicyDocument> documents;
    private List<Payment> payments;
    private List<BillingSchedule> billingSchedules;
    private List<PolicyStatusHistory> statusHistory;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
