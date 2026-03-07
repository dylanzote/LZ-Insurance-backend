package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.LifePolicyForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyLifeDetails {
    private String policyVersionId;
    private String insuredName;
    private LocalDate insuredDob;
    private BigDecimal sumAssured;
    private Integer termYears;
    private LifePolicyForm lifeType;
    private Map<String, Object> attributes;
    private List<PolicyLifeBeneficiary> beneficiaries;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
