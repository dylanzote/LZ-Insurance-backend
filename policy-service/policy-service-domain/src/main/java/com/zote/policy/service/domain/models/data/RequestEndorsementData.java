package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.EndorsementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEndorsementData {
    private String policyId;
    private EndorsementType type;
    private String description;
    private Map<String, Object> changes;
    private String requestedBy;
}
