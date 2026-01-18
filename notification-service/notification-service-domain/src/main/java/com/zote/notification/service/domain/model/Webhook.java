package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {
    
    private String id;
    private String userId;
    private String url;
    private String description;
    private List<String> eventTypes; // null = all events
    private String secret; // For HMAC signature verification
    private Map<String, String> headers; // Custom headers
    private boolean active;
    
    // Statistics
    private int successCount;
    private int failureCount;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastFailureAt;
    private String lastError;
    
    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}

