package com.zote.kafka.adapter.event;

import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String notificationId;
    private String userId;
    private LocalDateTime readAt;

    @Override
    public String getEventType() {
        return EventType.NOTIFICATION_READ.getCode();
    }
}
