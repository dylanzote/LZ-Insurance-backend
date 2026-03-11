package com.zote.kafka.adapter.event.policy;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.POLICY_CANCELLED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCancelledEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private String cancellationType;
    private String reason;
    private String cancelledBy;
    /** Pro-rata refund amount for billing (9.2). */
    private BigDecimal refundAmount;

    @Override
    public String getEventType() {
        return POLICY_CANCELLED.getCode();
    }
}
