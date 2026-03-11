package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.data.RequestEndorsementData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEndorsementRequest {

    @NotBlank
    private String policyId;

    @NotNull
    private EndorsementType type;

    private String description;

    @NotNull
    private Map<String, Object> changes;

    private String requestedBy;

    public RequestEndorsementData toData() {
        return RequestEndorsementData.builder()
                .policyId(policyId)
                .type(type)
                .description(description)
                .changes(changes)
                .requestedBy(requestedBy)
                .build();
    }
}
