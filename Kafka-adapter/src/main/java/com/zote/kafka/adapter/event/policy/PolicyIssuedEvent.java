package com.zote.kafka.adapter.event.policy;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.POLICY_ISSUED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyIssuedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;

    @Override
    public String getEventType() {
        return POLICY_ISSUED.getCode();
    }
}
