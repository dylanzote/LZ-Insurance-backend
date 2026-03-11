package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.SuspendPolicyData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspendPolicyRequest {

    @NotBlank
    private String policyId;

    private String reason;

    private LocalDate effectiveDate;

    private String suspendedBy;

    public SuspendPolicyData toData() {
        return SuspendPolicyData.builder()
                .policyId(policyId)
                .reason(reason)
                .cancellationDate(effectiveDate)
                .cancelledBy(suspendedBy)
                .build();
    }
}
