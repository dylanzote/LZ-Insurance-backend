package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.ApproveEndorsementData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveEndorsementRequest {

    @NotBlank
    private String endorsementRequestId;

    private String decidedBy;

    public ApproveEndorsementData toData() {
        return ApproveEndorsementData.builder()
                .endorsementRequestId(endorsementRequestId)
                .decidedBy(decidedBy)
                .build();
    }
}
