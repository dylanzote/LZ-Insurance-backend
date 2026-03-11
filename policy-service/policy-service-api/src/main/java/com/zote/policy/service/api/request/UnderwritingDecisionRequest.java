package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.data.UnderwritingDecisionData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionRequest {

    @NotBlank
    private String quoteId;

    @NotNull
    private UnderwritingDecisionStatus decision;

    private String reason;

    private String decidedBy;

    public UnderwritingDecisionData toData() {
        return UnderwritingDecisionData.builder()
                .quoteId(quoteId)
                .decision(decision)
                .reason(reason)
                .decidedBy(decidedBy)
                .build();
    }
}
