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
public class NotificationFailedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    private String notificationId;
    private String errorType;
    private String errorMessage;
    private Integer retryCount;

    @Override
    public String getEventType() {
        return EventType.NOTIFICATION_FAILED.getCode();
    }
}
