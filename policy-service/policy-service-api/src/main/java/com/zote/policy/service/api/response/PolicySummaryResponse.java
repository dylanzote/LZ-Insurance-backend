package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.PolicySummary;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PolicySummaryResponse {

    private String customerId;
    private Long totalPolicies;
    private Long activePolicies;
    private Long expiredPolicies;
    private Long cancelledPolicies;

    public static PolicySummaryResponse fromPolicySummary(PolicySummary summary) {
        PolicySummaryResponse response = new PolicySummaryResponse();
        BeanUtils.copyProperties(summary, response);
        return response;
    }
}
