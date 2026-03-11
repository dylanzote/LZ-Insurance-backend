package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.CancellationType;
import com.zote.policy.service.domain.models.data.RequestCancellationData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCancellationRequest {

    @NotBlank
    private String policyId;

    private String reason;

    private String requestedBy;

    private CancellationType cancellationType;

    public RequestCancellationData toData() {
        return RequestCancellationData.builder()
                .policyId(policyId)
                .reason(reason)
                .requestedBy(requestedBy)
                .cancellationType(cancellationType)
                .build();
    }
}
