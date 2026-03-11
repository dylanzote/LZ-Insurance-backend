package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePolicyData {
    private PolicyType type;
    private String productId;
    private String customerId;
    private String agentId;
    private String branchId;
    private BigDecimal premiumTotal;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private Map<String, Object> snapshot;
    private String createdBy;
}
