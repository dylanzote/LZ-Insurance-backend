package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quote {
    private String id;
    private String quoteNumber;
    private String productId;
    private PolicyType policyType;
    private String customerId;
    private String agentId;
    private String branchId;
    private BillingPlan billingPlan;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private BigDecimal premiumBeforeTax;
    private BigDecimal taxAmount;
    private BigDecimal premiumTotal;
    private QuoteStatus status;
    private LocalDateTime validUntil;
    private Map<String, Object> snapshot;
    private UnderwritingDecisionStatus underwritingDecision;
    private String underwritingReason;
    private String underwritingDecidedBy;
    private LocalDateTime underwritingDecidedAt;
    private Map<String, Object> ratingData;
    private List<SelectedCoverage> selectedCoverages;
    private List<CoveragePremium> coveragePremiums;
    /** Policy ID when this is a renewal quote - links to the policy being renewed */
    private String parentPolicyId;

    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
