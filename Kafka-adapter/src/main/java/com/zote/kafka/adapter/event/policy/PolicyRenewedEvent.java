package com.zote.kafka.adapter.event.policy;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.POLICY_RENEWED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRenewedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private String renewalQuoteId;
    private LocalDate newEffectiveDate;
    private LocalDate newExpiryDate;
    private BigDecimal renewalPremium;

    @Override
    public String getEventType() {
        return POLICY_RENEWED.getCode();
    }
}
