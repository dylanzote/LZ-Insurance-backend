package com.zote.notification.service.infrastructure.adapters.events.provider;

import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;

public interface UserEventHandler<T extends DataEvent> {

    void handle(T event);
    EventType getEventType();
}
