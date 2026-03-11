package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PaymentMethod;
import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.domain.models.Payment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private String id;
    private String policyId;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDate paymentDate;
    private String transactionId;
    private PaymentRecordStatus status;
    private Integer installmentNo;
    private LocalDateTime recordedAt;
    private String createdBy;
    private LocalDateTime updatedAt;

    public static PaymentResponse fromPayment(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        return response;
    }
}
