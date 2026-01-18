package com.zote.user.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {
    private String id;
    private String userId;
    private String action; // e.g., "login", "approve", "create"
    private String resource; // e.g., "auth", "claim", "policy"
    private String resourceId; // ID of the resource if applicable
    private String ipAddress;
    private String userAgent;
    private String status;
    private Long durationMs;
    private String additionalInfo;
    private LocalDateTime createdAt;
}

