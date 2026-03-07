package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyProductConfig {
    private String id;
    private String productId;
    private PolicyType policyType;
    private PolicyTermUnit termUnit;
    private Integer termLength;
    private BillingPlan defaultBillingPlan;
    private boolean allowFullPayment;
    private boolean allowInstallments;
    private Integer installmentsCount;
    private boolean requireDocuments;
    private boolean requiresUnderwriting;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
