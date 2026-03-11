package com.zote.kafka.adapter.event.policy;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.ENDORSEMENT_REJECTED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndorsementRejectedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String endorsementRequestId;
    private String policyId;
    private String decidedBy;
    private String rejectionReason;

    @Override
    public String getEventType() {
        return ENDORSEMENT_REJECTED.getCode();
    }
}
