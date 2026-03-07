package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyDocument {
    private String id;
    private String policyId;
    private PolicyDocumentType type;
    private String name;
    private String url;
    private PolicyDocumentStatus status;
    private LocalDateTime uploadedAt; // maps Auditable.createdAt
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private String rejectionReason;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
