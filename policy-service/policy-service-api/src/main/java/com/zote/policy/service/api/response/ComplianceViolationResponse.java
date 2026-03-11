package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.ComplianceViolation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceViolationResponse {

    private String id;
    private String policyId;
    private String violationType;
    private String description;
    private String severity;
    private String status;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;

    public static ComplianceViolationResponse from(ComplianceViolation v) {
        return ComplianceViolationResponse.builder()
                .id(v.getId())
                .policyId(v.getPolicyId())
                .violationType(v.getViolationType())
                .description(v.getDescription())
                .severity(v.getSeverity())
                .status(v.getStatus())
                .detectedAt(v.getDetectedAt())
                .resolvedAt(v.getResolvedAt())
                .resolvedBy(v.getResolvedBy())
                .resolutionNotes(v.getResolutionNotes())
                .build();
    }
}
