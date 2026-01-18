package com.zote.notification.service.infrastructure.adapters.events.provider;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class UserEventProvider {
    private final Map<EventType, UserEventHandler<?>> handlerMap = new EnumMap<>(EventType.class);

    public UserEventProvider(List<UserEventHandler<?>> eventHandlers) {
        eventHandlers.forEach(userEventHandler -> handlerMap.put(userEventHandler.getEventType(), userEventHandler));
    }

    public UserEventHandler<? extends DataEvent> getHandler(EventType type) {
        UserEventHandler<? extends DataEvent> handler = handlerMap.get(type);

        if (handler == null) {
            throw new IllegalStateException("No handler for event type: " + type);
        }
        return handler;
    }
}
