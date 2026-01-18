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
 * Domain event for user creation
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Language language;
    private String branchId;
    private String department;
    private boolean createdByAdmin;
    private String passwordResetToken;
    private int passwordResetTokenExpiryTime;
    private String createdByUserId;

    @Override
    public String getEventType() {
        return EventType.USER_CREATED.getCode();
    }
}

