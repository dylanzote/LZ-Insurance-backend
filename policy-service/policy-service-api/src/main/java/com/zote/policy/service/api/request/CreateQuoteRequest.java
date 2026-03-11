package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.SelectedCoverage;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateQuoteRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String customerId;

    private String agentId;

    private String branchId;

    @NotNull
    private BillingPlan billingPlan;

    @NotNull
    private LocalDate effectiveDate;

    private Map<String, Object> ratingData;

    private List<SelectedCoverage> selectedCoverages;

    public CreateQuoteData toData() {
        return CreateQuoteData.builder()
                .productId(productId)
                .customerId(customerId)
                .agentId(agentId)
                .branchId(branchId)
                .billingPlan(billingPlan)
                .effectiveDate(effectiveDate)
                .ratingData(ratingData)
                .selectedCoverages(selectedCoverages)
                .build();
    }
}
