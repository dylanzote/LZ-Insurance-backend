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
 * Domain event for password reset request
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Language language;
    
    // Password reset data
    private String passwordResetToken;
    private int passwordResetTokenExpiryTime; // in hours
    private boolean isCustomer; // Determines which portal URL to use

    @Override
    public String getEventType() {
        return EventType.PASSWORD_RESET_REQUESTED.getCode();
    }
}

