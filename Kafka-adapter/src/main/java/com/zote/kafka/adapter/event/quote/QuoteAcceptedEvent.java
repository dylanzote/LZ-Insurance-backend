package com.zote.kafka.adapter.event.quote;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.QUOTE_ACCEPTED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteAcceptedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String quoteId;
    private String quoteNumber;
    private String customerId;

    @Override
    public String getEventType() {
        return QUOTE_ACCEPTED.getCode();
    }
}
