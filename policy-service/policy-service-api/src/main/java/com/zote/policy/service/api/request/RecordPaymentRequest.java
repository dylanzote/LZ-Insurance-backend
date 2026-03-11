package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PaymentMethod;
import com.zote.policy.service.domain.models.data.RecordPaymentData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class RecordPaymentRequest {

    @NotBlank
    private String policyId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    @NotNull
    private LocalDate paymentDate;

    private String transactionId;

    private Integer installmentNo;

    private String recordedBy;

    public RecordPaymentData toData() {
        return RecordPaymentData.builder()
                .policyId(policyId)
                .amount(amount)
                .method(method)
                .paymentDate(paymentDate)
                .transactionId(transactionId)
                .installmentNo(installmentNo)
                .recordedBy(recordedBy)
                .build();
    }
}
