package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.PolicyStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyStatusHistoryResponse {

    private String id;
    private String policyId;
    private PolicyStatus fromStatus;
    private PolicyStatus toStatus;
    private String reason;
    private String changedBy;
    private LocalDateTime changedAt;

    public static PolicyStatusHistoryResponse from(PolicyStatusHistory h) {
        return PolicyStatusHistoryResponse.builder()
                .id(h.getId())
                .policyId(h.getPolicyId())
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .reason(h.getReason())
                .changedBy(h.getChangedBy())
                .changedAt(h.getChangedAt() != null ? h.getChangedAt() : h.getUpdatedAt())
                .build();
    }
}
