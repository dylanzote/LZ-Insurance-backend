package com.zote.notification.service.infrastructure.adapters.events.listener;

import com.zote.common.utils.exceptions.*;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.infrastructure.adapters.events.UserEventDeserializer;
import com.zote.notification.service.infrastructure.adapters.events.provider.UserEventHandler;
import com.zote.notification.service.infrastructure.adapters.events.provider.UserEventProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventDeserializer eventDeserializer;

    private final UserEventProvider userEventProvider;

    @KafkaListener(topics = "${notification.kafka.user-service-topic:user-events}", groupId = "${spring.kafka.consumer.group-id:notification-service-group}", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEvent(@Payload DataEvent event, Acknowledgment ack) {
        try {
            DataEvent domainEvent = eventDeserializer.deserialize(event);
            UserEventHandler<? extends DataEvent> provider = userEventProvider.getHandler(EventType.fromCode(domainEvent.getEventType()));
            invokeHandler(provider, event);
            ack.acknowledge();
        } catch (DuplicateNotificationException | RateLimitExceededException |
             ChannelDisabledException | QuietHoursException | UserNotFoundException e) {

            log.info("Skipping event: {}", e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("RETRYABLE error processing event", e);
            throw new FunctionalError("Infrastructure failure");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends DataEvent> void invokeHandler(UserEventHandler<? extends DataEvent> handler, DataEvent event) {
        ((UserEventHandler<T>) handler).handle((T) event);
    }
}
