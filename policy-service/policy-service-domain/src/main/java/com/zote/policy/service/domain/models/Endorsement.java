package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.EndorsementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Endorsement {
    private String id;
    private String policyId;
    private EndorsementType type;
    private String description;
    private LocalDate effectiveDate;
    private BigDecimal premiumChange;
    private Map<String, Object> changes;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
