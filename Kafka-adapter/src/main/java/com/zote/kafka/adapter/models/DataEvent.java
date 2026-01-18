package com.zote.kafka.adapter.models;

import java.time.LocalDateTime;

public interface DataEvent {
    String getEventId();
    LocalDateTime getOccurredAt();
    String getEventType();
    String getCorrelationId();
}
