package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySummary {
    private String customerId;
    private Long totalPolicies;
    private Long activePolicies;
    private Long expiredPolicies;
    private Long cancelledPolicies;
}
