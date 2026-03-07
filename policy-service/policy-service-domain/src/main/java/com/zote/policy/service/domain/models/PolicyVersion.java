package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyVersionReason;
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
public class PolicyVersion {
    private String id;
    private String policyId;
    private Integer versionNo;
    private PolicyVersionReason reason;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal premiumTotal;
    private Map<String, Object> snapshot;
    private List<PolicyCoverage> coverages;
    private PolicyAutoDetails autoDetails;
    private PolicyHomeDetails homeDetails;
    private PolicyLifeDetails lifeDetails;
    private PolicyHealthDetails healthDetails;
    private PolicyTravelDetails travelDetails;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
