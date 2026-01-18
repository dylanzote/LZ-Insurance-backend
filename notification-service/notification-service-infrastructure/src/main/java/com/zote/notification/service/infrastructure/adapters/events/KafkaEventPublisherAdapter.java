package com.zote.notification.service.infrastructure.adapters.events;

import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.service.EnhancedMessageProducer;
import com.zote.kafka.adapter.event.DomainEvent;
import com.zote.notification.service.domain.ports.outbound.service.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kafka-based implementation of EventPublisherPort
 * Publishes domain events to Kafka using the Kafka adapter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final EnhancedMessageProducer messageProducer;

    @Value("${notification.kafka.topic:notification-events}")
    private String notificationTopic;

    @Override
    public void publish(DataEvent event) {
        if (event == null) {
            log.warn("Attempted to publish null event");
            return;
        }

        try {
            // Convert DomainEvent to DataEvent adapter
            DataEvent dataEvent = new DomainEventAdapter(event);
            
            // Publish to Kafka asynchronously
            messageProducer.sendMessageAsync(notificationTopic, dataEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Successfully published event {} to topic {}", event.getEventId(), notificationTopic);
                    } else {
                        log.error("Failed to publish event {} to topic {}", event.getEventId(), notificationTopic, ex);
                    }
                });
        } catch (Exception e) {
            log.error("Error publishing event {} to Kafka", event.getEventId(), e);
        }
    }

    @Override
    public void publishAll(List<DataEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        log.info("Publishing {} events to topic {}", events.size(), notificationTopic);
        
        for (DataEvent event : events) {
            publish(event);
        }
    }

    /**
     * Adapter class to convert DomainEvent to DataEvent
     * Since both interfaces have the same structure, we can delegate
     */
    private static class DomainEventAdapter implements DataEvent {
        private final DataEvent domainEvent;

        public DomainEventAdapter(DataEvent domainEvent) {
            this.domainEvent = domainEvent;
        }

        @Override
        public String getEventId() {
            return domainEvent.getEventId();
        }

        @Override
        public java.time.LocalDateTime getOccurredAt() {
            return domainEvent.getOccurredAt();
        }

        @Override
        public String getEventType() {
            return domainEvent.getEventType();
        }

        @Override
        public String getCorrelationId() {
            return domainEvent.getCorrelationId();
        }
    }
}

