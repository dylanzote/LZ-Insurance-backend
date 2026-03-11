package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocumentResponse {

    private String id;
    private String policyId;
    private PolicyDocumentType type;
    private String name;
    private String url;
    private PolicyDocumentStatus status;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private String rejectionReason;

    public static PolicyDocumentResponse from(PolicyDocument doc) {
        return PolicyDocumentResponse.builder()
                .id(doc.getId())
                .policyId(doc.getPolicyId())
                .type(doc.getType())
                .name(doc.getName())
                .url(doc.getUrl())
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .verifiedAt(doc.getVerifiedAt())
                .verifiedBy(doc.getVerifiedBy())
                .rejectionReason(doc.getRejectionReason())
                .build();
    }
}
