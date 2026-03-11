package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PaymentMethod;
import com.zote.policy.service.domain.models.data.RecordFailedPaymentData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RecordFailedPaymentRequest {

    @NotBlank
    private String policyId;

    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    private LocalDate attemptedDate;

    @NotBlank
    private String failureReason;

    private String transactionId;

    private Integer installmentNo;

    private String recordedBy;

    public RecordFailedPaymentData toData() {
        return RecordFailedPaymentData.builder()
                .policyId(policyId)
                .amount(amount)
                .method(method)
                .attemptedDate(attemptedDate)
                .failureReason(failureReason)
                .transactionId(transactionId)
                .installmentNo(installmentNo)
                .recordedBy(recordedBy)
                .build();
    }
}
