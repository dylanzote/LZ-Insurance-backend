package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspendPolicyData {
    private String policyId;
    private String reason;
    private LocalDate cancellationDate;
    private String cancelledBy;
}
