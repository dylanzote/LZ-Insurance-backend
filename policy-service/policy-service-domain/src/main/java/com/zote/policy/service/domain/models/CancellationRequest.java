package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.domain.enums.CancellationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a customer or system request to cancel a policy (9.1).
 * Back-office approves/rejects to process (9.3).
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CancellationRequest {
    private String id;
    private String policyId;
    private CancellationType cancellationType;
    private String reason;
    private CancellationRequestStatus status;

    private String requestedBy;
    private LocalDateTime requestedAt;

    private String decidedBy;
    private LocalDateTime decidedAt;
    private String rejectionReason;

    /** Effective cancellation date (when policy ends). Null until approved. */
    private LocalDate effectiveDate;

    /** Pro-rata refund amount calculated at approval (9.2). */
    private BigDecimal refundAmount;
}
