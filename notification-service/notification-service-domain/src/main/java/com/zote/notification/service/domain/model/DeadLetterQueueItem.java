package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterQueueItem {
    private Long id;
    private String notificationId;
    private String providerId;
    private String errorType;
    private String errorMessage;
    private String errorStackTrace;
    private Map<String, Object> payload;
    private Integer retryCount;
    private LocalDateTime scheduledRetryAt;
    private Boolean processed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
