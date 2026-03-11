package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.CancellationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCancellationData {
    private String policyId;
    private String reason;
    private String requestedBy;
    private CancellationType cancellationType;
}
