package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.RenewPolicyData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewPolicyRequest {

    @NotBlank
    private String policyId;

    private String renewedBy;

    public RenewPolicyData toData() {
        return RenewPolicyData.builder()
                .policyId(policyId)
                .renewedBy(renewedBy)
                .build();
    }
}
