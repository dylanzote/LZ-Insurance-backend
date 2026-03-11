package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.IssuePolicyData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuePolicyRequest {

    @NotBlank
    private String policyId;

    private String issuedBy;

    public IssuePolicyData toData() {
        return IssuePolicyData.builder()
                .policyId(policyId)
                .issuedBy(issuedBy)
                .build();
    }
}
