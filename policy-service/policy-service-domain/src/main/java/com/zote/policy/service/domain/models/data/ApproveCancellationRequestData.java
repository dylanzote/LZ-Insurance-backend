package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveCancellationRequestData {
    private String cancellationRequestId;
    private String decidedBy;
    /** Effective date when cancellation takes effect. If null, uses today. */
    private LocalDate effectiveDate;
}
