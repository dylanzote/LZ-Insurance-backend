package com.zote.kafka.adapter.event;

import com.zote.kafka.adapter.models.DataEvent;

/**
 * Base interface for all domain events in the system
 * Domain events represent business occurrences that have already happened
 * Extends DataEvent to ensure compatibility with Kafka serialization
 */
public interface DomainEvent extends DataEvent {
    // All methods inherited from DataEvent:
    // - String getEventId();
    // - LocalDateTime getOccurredAt();
    // - String getEventType();
    // - String getCorrelationId();
}
