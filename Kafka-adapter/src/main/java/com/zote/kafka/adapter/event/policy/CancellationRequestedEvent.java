package com.zote.kafka.adapter.event.policy;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.CANCELLATION_REQUESTED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRequestedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String cancellationRequestId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private String cancellationType;
    private String reason;
    private String requestedBy;

    @Override
    public String getEventType() {
        return CANCELLATION_REQUESTED.getCode();
    }
}
