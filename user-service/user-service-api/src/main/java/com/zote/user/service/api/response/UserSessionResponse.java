package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    
    private String sessionId;
    private String userId;
    private String deviceType; // WEB, MOBILE_ANDROID, MOBILE_IOS
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private boolean isCurrentSession;
}

