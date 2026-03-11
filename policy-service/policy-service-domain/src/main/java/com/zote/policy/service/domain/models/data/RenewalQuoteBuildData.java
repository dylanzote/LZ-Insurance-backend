package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.BillingPlan;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewalQuoteBuildData {
    private Policy policy;
    private PolicyVersion currentVersion;
    private LocalDate newEffectiveDate;
    private LocalDate newExpiryDate;
    private BigDecimal renewalPremium;
    private BigDecimal premiumBeforeTax;
    private BigDecimal taxAmount;
    private int validityDays;
    private String renewalQuoteNumberPrefix;
    private Map<String, Object> snapshot;
}
