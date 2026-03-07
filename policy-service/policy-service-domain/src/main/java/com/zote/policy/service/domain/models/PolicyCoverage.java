package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyCoverage {
    private String id;
    private String policyVersionId;
    private String coverageCode;
    private BigDecimal limitAmount;
    private BigDecimal deductibleAmount;
    private BigDecimal premiumPortion;
    private Map<String, Object> attributes;
}
