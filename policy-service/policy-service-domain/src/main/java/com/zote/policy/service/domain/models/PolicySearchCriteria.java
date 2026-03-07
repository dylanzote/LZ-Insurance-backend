package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySearchCriteria {
    private PolicyStatus status;
    private PolicyType type;
    private String customerId;
    private String agentId;
    private String branchId;
    private String search; // policy number / product id / etc.
    private LocalDate activeOnDate;
    private LocalDate expiringFrom;
    private LocalDate expiringTo;
}
