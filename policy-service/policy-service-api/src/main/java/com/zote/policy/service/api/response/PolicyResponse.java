package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.SaleSource;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.Policy;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PolicyResponse {

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
    private SaleSource source;
    private String parentPolicyId;
    private Integer gracePeriodDays;
    private LocalDateTime issuedAt;
    private String timezone;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static PolicyResponse fromPolicy(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        BeanUtils.copyProperties(policy, response);
        return response;
    }
}
