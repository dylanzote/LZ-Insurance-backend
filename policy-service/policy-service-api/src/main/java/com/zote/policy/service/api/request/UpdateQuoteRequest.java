package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.SelectedCoverage;
import com.zote.policy.service.domain.models.data.UpdateQuoteData;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateQuoteRequest {

    @NotBlank
    private String quoteId;

    private BillingPlan billingPlan;

    private LocalDate effectiveDate;

    private Map<String, Object> ratingData;

    private List<SelectedCoverage> selectedCoverages;

    private String lastModifiedBy;

    public UpdateQuoteData toData() {
        return UpdateQuoteData.builder()
                .quoteId(quoteId)
                .billingPlan(billingPlan)
                .effectiveDate(effectiveDate)
                .ratingData(ratingData)
                .selectedCoverages(selectedCoverages)
                .lastModifiedBy(lastModifiedBy)
                .build();
    }
}
