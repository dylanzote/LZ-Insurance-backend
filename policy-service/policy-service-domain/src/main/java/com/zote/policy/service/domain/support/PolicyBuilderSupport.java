package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.PolicyVersionReason;
import com.zote.policy.service.domain.enums.SaleSource;
import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.data.BuildPolicyFromQuoteData;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class PolicyBuilderSupport {

    public Policy buildPolicyFromQuote(BuildPolicyFromQuoteData data) {
        var quote = data.getQuote();
        if (quote.getEffectiveDate() != null && quote.getExpiryDate() != null
                && quote.getEffectiveDate().isAfter(quote.getExpiryDate())) {
            throw new FunctionalError("effectiveDate must be before or equal to expiryDate");
        }
        SaleSource source = quote.getAgentId() != null && !quote.getAgentId().isBlank()
                ? SaleSource.AGENT
                : SaleSource.API;
        String currency = data.getCurrency() != null && !data.getCurrency().isBlank() ? data.getCurrency() : "XAF";
        String timezone = data.getTimezone() != null && !data.getTimezone().isBlank() ? data.getTimezone() : "Africa/Douala";
        return Policy.builder()
                .id(UUID.randomUUID().toString())
                .policyNumber(data.getPolicyNumber())
                .productId(required(quote.getProductId(), "productId"))
                .customerId(required(quote.getCustomerId(), "customerId"))
                .agentId(quote.getAgentId())
                .branchId(quote.getBranchId())
                .productConfigId(required(data.getProductConfigId(), "productConfigId"))
                .billingPlan(quote.getBillingPlan())
                .type(quote.getPolicyType())
                .status(data.getPolicyStatus())
                .paymentStatus(PaymentStatus.PENDING)
                .currency(currency)
                .premiumTotal(quote.getPremiumTotal())
                .effectiveDate(quote.getEffectiveDate())
                .expiryDate(quote.getExpiryDate())
                .source(source)
                .timezone(timezone)
                .build();
    }

    public PolicyVersion policyVersionBuilderForEndorsement(String policyId, int versionNo,
                                                           Map<String, Object> snapshot, BigDecimal newPremium) {
        return PolicyVersion.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .versionNo(versionNo)
                .reason(PolicyVersionReason.ENDORSEMENT)
                .effectiveFrom(LocalDate.now())
                .effectiveTo(null)
                .premiumTotal(newPremium)
                .snapshot(snapshot)
                .build();
    }

    public PolicyVersion policyVersionBuilderForRenewal(String policyId, int versionNo,
                                                        Map<String, Object> snapshot, BigDecimal newPremium,
                                                        LocalDate effectiveFrom, LocalDate effectiveTo) {
        return PolicyVersion.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .versionNo(versionNo)
                .reason(PolicyVersionReason.RENEWAL)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .premiumTotal(newPremium)
                .snapshot(snapshot)
                .build();
    }

    public PolicyVersion policyVersionBuilder(Policy policy, Map<String, Object> snapshot) {
        return PolicyVersion.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policy.getId())
                .versionNo(1)
                .reason(PolicyVersionReason.NEW_BUSINESS)
                .effectiveFrom(policy.getEffectiveDate())
                .effectiveTo(null)
                .premiumTotal(policy.getPremiumTotal())
                .snapshot(snapshot)
                .build();
    }

    public PolicyStatusHistory policyStatusHistoryBuilder(String policyId, PolicyStatus from, PolicyStatus to, String reason) {
        return policyStatusHistoryBuilder(policyId, from, to, reason, null);
    }

    public PolicyStatusHistory policyStatusHistoryBuilder(String policyId, PolicyStatus from, PolicyStatus to, String reason, String changedBy) {
        return PolicyStatusHistory.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .fromStatus(from)
                .toStatus(to)
                .reason(reason)
                .changedBy(changedBy)
                .build();
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new FunctionalError(field + " is required");
        }
        return value;
    }
}
