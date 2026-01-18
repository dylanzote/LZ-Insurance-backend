package com.zote.notification.service.domain.ports.outbound.service;


import com.zote.kafka.adapter.event.DomainEvent;
import com.zote.kafka.adapter.models.DataEvent;

import java.util.List;

public interface EventPublisherPort {
    void publish(DataEvent event);
    void publishAll(List<DataEvent> events);
}
