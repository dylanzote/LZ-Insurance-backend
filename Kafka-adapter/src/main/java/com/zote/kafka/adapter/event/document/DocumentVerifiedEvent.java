package com.zote.kafka.adapter.event.document;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.zote.kafka.adapter.models.EventType.DOCUMENT_VERIFIED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerifiedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String documentId;
    private String policyId;
    private String policyNumber;
    private String customerId;
    private String documentType;
    private String verifiedBy;

    @Override
    public String getEventType() {
        return DOCUMENT_VERIFIED.getCode();
    }
}
