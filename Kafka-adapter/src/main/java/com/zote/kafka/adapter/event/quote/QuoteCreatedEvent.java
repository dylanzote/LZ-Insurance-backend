package com.zote.kafka.adapter.event.quote;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.QUOTE_CREATED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteCreatedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String quoteId;
    private String quoteNumber;
    private String customerId;
    private String productId;

    @Override
    public String getEventType() {
        return QUOTE_CREATED.getCode();
    }
}
