package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionData {
    private String quoteId;
    private UnderwritingDecisionStatus decision;
    private String reason;
    private String decidedBy;
}
