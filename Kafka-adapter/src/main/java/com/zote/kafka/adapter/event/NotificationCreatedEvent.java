package com.zote.kafka.adapter.event;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
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
public class NotificationCreatedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String notificationId;
    private String userId;
    private NotificationChannel channel;
    private NotificationPriority priority;

    @Override
    public String getEventType() {
        return EventType.NOTIFICATION_CREATED.getCode();
    }
}
