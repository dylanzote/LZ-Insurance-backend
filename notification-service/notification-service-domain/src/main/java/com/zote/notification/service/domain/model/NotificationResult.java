package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResult {
    private boolean success;
    private String messageId;
    private String error;
    private Long latencyMs;

    public static NotificationResult success(String messageId) {
        return NotificationResult.builder()
            .success(true)
            .messageId(messageId)
            .build();
    }

    public static NotificationResult failed(String error) {
        return NotificationResult.builder()
            .success(false)
            .error(error)
            .build();
    }

    public NotificationResult latencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
        return this;
    }
}
