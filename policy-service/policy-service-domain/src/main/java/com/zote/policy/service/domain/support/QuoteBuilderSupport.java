package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.RatingResult;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.models.data.RenewalQuoteBuildData;
import com.zote.policy.service.domain.models.data.UpdateQuoteApplyData;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class QuoteBuilderSupport {

    public Quote buildQuote(CreateQuoteData data,
                            Product product,
                            LocalDate expiryDate,
                            RatingResult ratingResult,
                            Map<String, Object> snapshot,
                            int quoteValidityDays,
                            String quoteNumberPrefix) {
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .quoteNumber(generateQuoteNumber(quoteNumberPrefix))
                .productId(product.getId())
                .policyType(product.getPolicyType())
                .customerId(data.getCustomerId())
                .agentId(data.getAgentId())
                .branchId(data.getBranchId())
                .billingPlan(data.getBillingPlan())
                .effectiveDate(data.getEffectiveDate())
                .expiryDate(expiryDate)
                .ratingData(data.getRatingData())
                .selectedCoverages(data.getSelectedCoverages())
                .coveragePremiums(ratingResult.getCoveragePremiums())
                .premiumBeforeTax(ratingResult.getPremiumBeforeTax())
                .taxAmount(ratingResult.getTaxAmount())
                .premiumTotal(ratingResult.getPremiumTotal())
                .status(QuoteStatus.CALCULATED)
                .underwritingDecision(UnderwritingDecisionStatus.PENDING)
                .validUntil(LocalDateTime.now().plusDays(quoteValidityDays))
                .snapshot(snapshot)
                .build();
    }

    private String generateQuoteNumber(String prefix) {
        String p = (prefix != null && !prefix.isBlank()) ? prefix : "Q-POL";
        return p + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    public Quote buildRenewalQuote(RenewalQuoteBuildData data) {
        String quoteNumber = generateRenewalQuoteNumber(data.getRenewalQuoteNumberPrefix());
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .quoteNumber(quoteNumber)
                .parentPolicyId(data.getPolicy().getId())
                .productId(data.getPolicy().getProductId())
                .policyType(data.getPolicy().getType())
                .customerId(data.getPolicy().getCustomerId())
                .agentId(data.getPolicy().getAgentId())
                .branchId(data.getPolicy().getBranchId())
                .billingPlan(data.getPolicy().getBillingPlan())
                .effectiveDate(data.getNewEffectiveDate())
                .expiryDate(data.getNewExpiryDate())
                .premiumBeforeTax(data.getPremiumBeforeTax())
                .taxAmount(data.getTaxAmount())
                .premiumTotal(data.getRenewalPremium())
                .status(QuoteStatus.APPROVED)
                .underwritingDecision(UnderwritingDecisionStatus.APPROVED)
                .validUntil(LocalDateTime.now().plusDays(data.getValidityDays()))
                .snapshot(data.getSnapshot())
                .build();
    }

    private String generateRenewalQuoteNumber(String prefix) {
        String p = (prefix != null && !prefix.isBlank()) ? prefix : "REN";
        return p + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    public Map<String, Object> buildSnapshot(CreateQuoteData data, Product product) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("productId", product.getId());
        snapshot.put("productCode", product.getCode());
        snapshot.put("productName", product.getName());
        snapshot.put("policyType", product.getPolicyType());
        snapshot.put("effectiveDate", data.getEffectiveDate());
        snapshot.put("billingPlan", data.getBillingPlan());
        snapshot.put("ratingData", data.getRatingData());
        snapshot.put("selectedCoverages", data.getSelectedCoverages());
        return snapshot;
    }

    /**
     * Applies update data to an existing quote, preserving id, quoteNumber, and other immutable fields.
     */
    public Quote applyQuoteUpdate(Quote existing, UpdateQuoteApplyData applyData) {
        return Quote.builder()
                .id(existing.getId())
                .quoteNumber(existing.getQuoteNumber())
                .productId(existing.getProductId())
                .policyType(existing.getPolicyType())
                .customerId(existing.getCustomerId())
                .agentId(existing.getAgentId())
                .branchId(existing.getBranchId())
                .billingPlan(applyData.getBillingPlan())
                .effectiveDate(applyData.getEffectiveDate())
                .expiryDate(applyData.getExpiryDate())
                .ratingData(applyData.getRatingData())
                .selectedCoverages(applyData.getSelectedCoverages())
                .coveragePremiums(applyData.getCoveragePremiums())
                .premiumBeforeTax(applyData.getPremiumBeforeTax())
                .taxAmount(applyData.getTaxAmount())
                .premiumTotal(applyData.getPremiumTotal())
                .status(existing.getStatus())
                .underwritingDecision(existing.getUnderwritingDecision())
                .underwritingReason(existing.getUnderwritingReason())
                .underwritingDecidedBy(existing.getUnderwritingDecidedBy())
                .underwritingDecidedAt(existing.getUnderwritingDecidedAt())
                .validUntil(existing.getValidUntil())
                .snapshot(applyData.getSnapshot())
                .createdBy(existing.getCreatedBy())
                .createdAt(existing.getCreatedAt())
                .lastModifiedBy(applyData.getLastModifiedBy())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
