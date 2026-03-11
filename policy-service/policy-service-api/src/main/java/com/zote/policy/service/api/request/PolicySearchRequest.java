package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.PolicySearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySearchRequest {

    private PolicyStatus status;
    private PolicyType type;
    private String customerId;
    private String agentId;
    private String branchId;
    private String search;
    private LocalDate activeOnDate;
    private LocalDate expiringFrom;
    private LocalDate expiringTo;
    private BigDecimal premiumMin;
    private BigDecimal premiumMax;

    public PolicySearchCriteria toCriteria() {
        return PolicySearchCriteria.builder()
                .status(status)
                .type(type)
                .customerId(customerId)
                .agentId(agentId)
                .branchId(branchId)
                .search(search)
                .activeOnDate(activeOnDate)
                .expiringFrom(expiringFrom)
                .expiringTo(expiringTo)
                .premiumMin(premiumMin)
                .premiumMax(premiumMax)
                .build();
    }
}
