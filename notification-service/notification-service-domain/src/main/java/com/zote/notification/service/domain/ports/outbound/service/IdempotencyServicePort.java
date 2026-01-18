package com.zote.notification.service.domain.ports.outbound.service;

import java.util.Map;

public interface IdempotencyServicePort {
    boolean isDuplicate(String idempotencyKey, String userId);
    void storeIdempotencyKey(String idempotencyKey, String userId, String notificationId);
    void cleanupOldKeys(int daysToKeep);
    Map<String, String> getRecentIdempotencyKeys(String userId, int limit);
}
