package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.common.utils.enums.NotificationChannel;

import java.util.Map;

public interface RateLimiterPort {
    boolean acquireToken(String userId, NotificationChannel channel);
    boolean acquireToken(String userId, String providerId);
    void resetBucket(String userId, NotificationChannel channel);
    Map<String, Long> getRateLimitStats(String userId);
    Map<String, Long> getProviderRateLimitStats(String providerId);
}
