package com.zote.policy.service.domain.models;

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
public class PolicyTravelDetails {
    private String policyVersionId;
    private LocalDate tripStart;
    private LocalDate tripEnd;
    private String destination;
    private BigDecimal tripCost;
    private Map<String, Object> attributes;
    private List<PolicyTravelTraveller> travellers;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
