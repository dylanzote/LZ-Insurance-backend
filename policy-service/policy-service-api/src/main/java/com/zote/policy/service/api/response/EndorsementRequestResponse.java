package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.EndorsementRequest;
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
public class EndorsementRequestResponse {

    private String id;
    private String policyId;
    private EndorsementType type;
    private String description;
    private Map<String, Object> changes;
    private EndorsementRequestStatus status;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private String decidedBy;
    private LocalDateTime decidedAt;
    private String rejectionReason;
    private String endorsementId;

    public static EndorsementRequestResponse fromEndorsementRequest(EndorsementRequest request) {
        return EndorsementRequestResponse.builder()
                .id(request.getId())
                .policyId(request.getPolicyId())
                .type(request.getType())
                .description(request.getDescription())
                .changes(request.getChanges())
                .status(request.getStatus())
                .requestedBy(request.getRequestedBy())
                .requestedAt(request.getRequestedAt())
                .decidedBy(request.getDecidedBy())
                .decidedAt(request.getDecidedAt())
                .rejectionReason(request.getRejectionReason())
                .endorsementId(request.getEndorsementId())
                .build();
    }
}
