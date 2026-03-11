package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.RejectEndorsementData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectEndorsementRequest {

    @NotBlank
    private String endorsementRequestId;

    private String decidedBy;

    private String rejectionReason;

    public RejectEndorsementData toData() {
        return RejectEndorsementData.builder()
                .endorsementRequestId(endorsementRequestId)
                .decidedBy(decidedBy)
                .rejectionReason(rejectionReason)
                .build();
    }
}
