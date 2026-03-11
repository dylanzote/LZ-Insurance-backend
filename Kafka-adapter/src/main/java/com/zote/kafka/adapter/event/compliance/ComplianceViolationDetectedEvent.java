package com.zote.kafka.adapter.event.compliance;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import static com.zote.kafka.adapter.models.EventType.COMPLIANCE_VIOLATION_DETECTED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceViolationDetectedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String violationId;
    private String policyId;
    private String violationType;
    private String description;
    private String severity;
    private Map<String, Object> metadata;

    @Override
    public String getEventType() {
        return COMPLIANCE_VIOLATION_DETECTED.getCode();
    }
}
