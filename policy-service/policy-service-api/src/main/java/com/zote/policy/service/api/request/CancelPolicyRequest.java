package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.CancellationType;
import com.zote.policy.service.domain.models.data.CancelPolicyData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPolicyRequest {

    @NotBlank
    private String policyId;

    private String reason;

    private String cancelledBy;

    private CancellationType cancellationType;

    public CancelPolicyData toData() {
        return CancelPolicyData.builder()
                .policyId(policyId)
                .reason(reason)
                .cancelledBy(cancelledBy)
                .cancellationType(cancellationType)
                .build();
    }
}
