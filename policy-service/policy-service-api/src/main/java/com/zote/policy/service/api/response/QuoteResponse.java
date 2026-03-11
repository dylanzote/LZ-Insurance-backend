package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.Quote;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QuoteResponse {

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
    private UnderwritingDecisionStatus underwritingDecision;
    private String underwritingReason;
    private String underwritingDecidedBy;
    private LocalDateTime underwritingDecidedAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static QuoteResponse fromQuote(Quote quote) {
        QuoteResponse response = new QuoteResponse();
        BeanUtils.copyProperties(quote, response);
        return response;
    }
}
