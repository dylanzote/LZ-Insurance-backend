package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyStatusHistory {
    private String id;
    private String policyId;
    private PolicyStatus fromStatus;
    private PolicyStatus toStatus;
    private String reason;
    private String changedBy;
    private LocalDateTime changedAt; // maps Auditable.createdAt
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
