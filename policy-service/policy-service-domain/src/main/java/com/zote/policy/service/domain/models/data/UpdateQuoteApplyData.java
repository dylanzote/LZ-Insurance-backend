package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.CoveragePremium;
import com.zote.policy.service.domain.models.RatingResult;
import com.zote.policy.service.domain.models.SelectedCoverage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Parameter object for applying recalculated values to an existing quote during update.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuoteApplyData {
    private BillingPlan billingPlan;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private Map<String, Object> ratingData;
    private List<SelectedCoverage> selectedCoverages;
    private List<CoveragePremium> coveragePremiums;
    private BigDecimal premiumBeforeTax;
    private BigDecimal taxAmount;
    private BigDecimal premiumTotal;
    private Map<String, Object> snapshot;
    private String lastModifiedBy;
}
