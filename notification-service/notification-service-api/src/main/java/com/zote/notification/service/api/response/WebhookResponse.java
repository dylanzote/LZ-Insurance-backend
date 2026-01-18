package com.zote.notification.service.api.response;

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
public class WebhookResponse {
    
    private String id;
    private String userId;
    private String url;
    private String description;
    private List<String> eventTypes;
    private boolean active;
    private Map<String, String> headers;
    
    private int successCount;
    private int failureCount;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastFailureAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

