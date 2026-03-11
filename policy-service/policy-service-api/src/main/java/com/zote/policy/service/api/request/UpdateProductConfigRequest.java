package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.data.UpdateProductConfigData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductConfigRequest {

    @NotBlank
    private String id;

    @NotBlank
    private String productId;

    @NotNull
    private PolicyType policyType;

    @NotNull
    private PolicyTermUnit termUnit;

    @Positive
    private Integer termLength;

    @NotNull
    private BillingPlan defaultBillingPlan;

    private boolean allowFullPayment;

    private boolean allowInstallments;

    private Integer installmentsCount;

    private boolean requireDocuments;

    private boolean requiresUnderwriting;

    private String lastModifiedBy;

    public UpdateProductConfigData toData() {
        return UpdateProductConfigData.builder()
                .id(id)
                .productId(productId)
                .policyType(policyType)
                .termUnit(termUnit)
                .termLength(termLength)
                .defaultBillingPlan(defaultBillingPlan)
                .allowFullPayment(allowFullPayment)
                .allowInstallments(allowInstallments)
                .installmentsCount(installmentsCount)
                .requireDocuments(requireDocuments)
                .requiresUnderwriting(requiresUnderwriting)
                .lastModifiedBy(lastModifiedBy)
                .build();
    }
}
