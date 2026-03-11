package com.zote.kafka.adapter.event.quote;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.UNDERWRITING_DECISION;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String quoteId;
    private String quoteNumber;
    private String customerId;
    private String decision;
    private String reason;
    private String decidedBy;

    @Override
    public String getEventType() {
        return UNDERWRITING_DECISION.getCode();
    }
}
