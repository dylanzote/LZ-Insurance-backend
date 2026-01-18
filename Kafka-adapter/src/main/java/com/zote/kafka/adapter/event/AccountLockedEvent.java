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
 * Domain event for account lock due to too many failed login attempts
 * Published by user-service, consumed by notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLockedEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;
    
    // User data
    private String userId;
    private String email;
    private String firstName;
    private Language language;
    
    // Lock details
    private int failedAttempts; // Total failed attempts that led to lock
    private String ipAddress; // IP address of last failed attempt
    private String userAgent; // Browser/device info
    private LocalDateTime lockedAt; // When the account was locked
    private LocalDateTime unlockAt; // When the account will be unlocked (if temporary)
    private String unlockToken; // Token to unlock account

    @Override
    public String getEventType() {
        return EventType.ACCOUNT_LOCKED.getCode();
    }
}

