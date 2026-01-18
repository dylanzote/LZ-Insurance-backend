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
 * Domain event for email verification request
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequestEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Language language;
    
    // Email verification data
    private String verificationToken;
    private int tokenExpiryHours; // in hours
    private boolean isCustomer; // Determines which portal URL to use

    @Override
    public String getEventType() {
        return EventType.EMAIL_VERIFICATION_REQUESTED.getCode();
    }
}


