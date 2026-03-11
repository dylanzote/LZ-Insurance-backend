package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.SelectedCoverage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuoteData {
    private String quoteId;
    private BillingPlan billingPlan;
    private LocalDate effectiveDate;
    private Map<String, Object> ratingData;
    private List<SelectedCoverage> selectedCoverages;
    private String lastModifiedBy;
}
