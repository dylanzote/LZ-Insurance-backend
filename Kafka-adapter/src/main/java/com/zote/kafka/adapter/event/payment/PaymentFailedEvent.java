package com.zote.kafka.adapter.event.payment;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.PAYMENT_FAILED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String paymentId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private BigDecimal amount;
    private String method;
    private LocalDate attemptedDate;
    private String failureReason;
    private String transactionId;
    private Integer installmentNo;

    @Override
    public String getEventType() {
        return PAYMENT_FAILED.getCode();
    }
}
