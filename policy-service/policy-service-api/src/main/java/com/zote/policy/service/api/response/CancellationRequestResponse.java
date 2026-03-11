package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.domain.enums.CancellationType;
import com.zote.policy.service.domain.models.CancellationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRequestResponse {

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
    private LocalDate effectiveDate;
    private BigDecimal refundAmount;

    public static CancellationRequestResponse fromCancellationRequest(CancellationRequest request) {
        return CancellationRequestResponse.builder()
                .id(request.getId())
                .policyId(request.getPolicyId())
                .cancellationType(request.getCancellationType())
                .reason(request.getReason())
                .status(request.getStatus())
                .requestedBy(request.getRequestedBy())
                .requestedAt(request.getRequestedAt())
                .decidedBy(request.getDecidedBy())
                .decidedAt(request.getDecidedAt())
                .rejectionReason(request.getRejectionReason())
                .effectiveDate(request.getEffectiveDate())
                .refundAmount(request.getRefundAmount())
                .build();
    }
}
