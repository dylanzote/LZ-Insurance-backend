package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectCancellationRequestData {
    private String cancellationRequestId;
    private String decidedBy;
    private String rejectionReason;
}
