package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.BillingPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductConfigData {
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
}
