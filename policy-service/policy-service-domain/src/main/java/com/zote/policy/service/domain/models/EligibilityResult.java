package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EligibilityResult {
    private boolean eligible;
    private boolean requiresManualReview;
    private String reason;
}
