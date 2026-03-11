package com.zote.kafka.adapter.event.payment;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.PAYMENT_OVERDUE;

/**
 * Published when billing schedules are marked overdue (10.5, 10.6).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOverdueEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private int overdueInstallmentCount;
    private BigDecimal totalOverdueAmount;
    private LocalDate nextDueDate;

    @Override
    public String getEventType() {
        return PAYMENT_OVERDUE.getCode();
    }
}
