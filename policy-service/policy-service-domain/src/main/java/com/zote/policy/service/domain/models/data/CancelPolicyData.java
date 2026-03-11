package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.CancellationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPolicyData {
    private String policyId;
    private String reason;
    private String cancelledBy;
    private CancellationType cancellationType;
    /** Effective cancellation date. If null, uses today (9.4). */
    private LocalDate effectiveDate;
    /** Pro-rata refund amount for billing (9.2). */
    private BigDecimal refundAmount;
}
