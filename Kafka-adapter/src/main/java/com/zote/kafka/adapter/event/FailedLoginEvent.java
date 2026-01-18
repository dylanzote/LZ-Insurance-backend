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
 * Domain event for failed login attempt
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedLoginEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private Language language;
    
    // Failed login details
    private int failedAttempts; // Current count of failed attempts
    private String ipAddress; // IP address where failed login occurred
    private String userAgent; // Browser/device info
    private LocalDateTime attemptedAt; // When the failed attempt occurred

    @Override
    public String getEventType() {
        return EventType.FAILED_LOGIN.getCode();
    }
}

