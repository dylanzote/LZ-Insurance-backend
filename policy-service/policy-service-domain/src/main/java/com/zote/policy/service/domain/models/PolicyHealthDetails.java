package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyHealthDetails {
    private String policyVersionId;
    private String planCode;
    private String tier;
    private BigDecimal deductible;
    private BigDecimal copay;
    private Map<String, Object> attributes;
    private List<PolicyHealthMember> members;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
