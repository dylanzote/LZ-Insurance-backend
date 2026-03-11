package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.ReinstatePolicyData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReinstatePolicyRequest {

    @NotBlank
    private String policyId;

    private String reason;

    private String reinstatedBy;

    public ReinstatePolicyData toData() {
        return ReinstatePolicyData.builder()
                .policyId(policyId)
                .reason(reason)
                .reinstatedBy(reinstatedBy)
                .build();
    }
}
