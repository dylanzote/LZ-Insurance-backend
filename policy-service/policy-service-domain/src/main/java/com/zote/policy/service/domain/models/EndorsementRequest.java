package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.domain.enums.EndorsementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EndorsementRequest {
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

    /** Set when approved and applied - links to the created Endorsement */
    private String endorsementId;
}
