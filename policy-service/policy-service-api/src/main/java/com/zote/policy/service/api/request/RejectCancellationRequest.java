package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.RejectCancellationRequestData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectCancellationRequest {

    @NotBlank
    private String cancellationRequestId;

    private String decidedBy;

    private String rejectionReason;

    public RejectCancellationRequestData toData() {
        return RejectCancellationRequestData.builder()
                .cancellationRequestId(cancellationRequestId)
                .decidedBy(decidedBy)
                .rejectionReason(rejectionReason)
                .build();
    }
}
