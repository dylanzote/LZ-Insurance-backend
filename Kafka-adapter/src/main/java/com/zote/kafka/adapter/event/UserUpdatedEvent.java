package com.zote.kafka.adapter.event;

import com.zote.common.utils.enums.Language;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event for user profile update
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Language language;
    private String updatedFields; // Comma-separated list of updated fields

    @Override
    public String getEventType() {
        return EventType.USER_UPDATED.getCode();
    }
}

