package com.zote.notification.service.infrastructure.service;

import com.zote.common.utils.config.IdempotencyProperties;
import com.zote.notification.service.domain.ports.outbound.service.IdempotencyServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotencyService implements IdempotencyServicePort {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    private final IdempotencyProperties properties;

    // Local cache for single instance deployments
    private final Map<String, String> localCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> localCacheTimestamps = new ConcurrentHashMap<>();

    @Override
    public boolean isDuplicate(String idempotencyKey, String userId) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return false;
        }

        String cacheKey = buildCacheKey(idempotencyKey, userId);

        // Try Redis first
        if (isRedisAvailable() && redisTemplate != null) {
            try {
                Object existing = redisTemplate.opsForValue().get(cacheKey);
                return existing != null;
            } catch (Exception e) {
                log.warn("Failed to check idempotency in Redis, using local cache: {}", e.getMessage());
            }
        }

        // Fallback to local cache
        synchronized (localCache) {
            // Clean old entries
            cleanupLocalCache();

            String existing = localCache.get(cacheKey);
            if (existing != null) {
                // Check if entry is still valid
                LocalDateTime timestamp = localCacheTimestamps.get(cacheKey);
                if (timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(24))) {
                    return true;
                } else {
                    // Entry expired, remove it
                    localCache.remove(cacheKey);
                    localCacheTimestamps.remove(cacheKey);
                }
            }

            return false;
        }
    }

    @Override
    public void storeIdempotencyKey(String idempotencyKey, String userId, String notificationId) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return;
        }

        String cacheKey = buildCacheKey(idempotencyKey, userId);

        // Store in Redis with TTL
        if (isRedisAvailable() && redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(
                        cacheKey,
                        notificationId,
                        Duration.ofHours(properties.getTtlHours())
                );
                log.debug("Stored idempotency key in Redis: {}", cacheKey);
                return;
            } catch (Exception e) {
                log.warn("Failed to store idempotency key in Redis, using local cache: {}", e.getMessage());
            }
        }
        
        // Fallback to local cache
        storeInLocalCache(cacheKey, notificationId);
    }

    @Override
    public void cleanupOldKeys(int daysToKeep) {
        log.info("Cleaning up old idempotency keys (older than {} days)", daysToKeep);

        if (isRedisAvailable()) {
            // Redis handles TTL automatically
            log.debug("Redis TTL handles cleanup automatically");
        } else {
            cleanupLocalCache();
        }
    }

    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM daily
    public void scheduledCleanup() {
        // Use default TTL from properties
        cleanupOldKeys(properties.getTtlHours() / 24); // Convert hours to days
    }

    @Override
    public Map<String, String> getRecentIdempotencyKeys(String userId, int limit) {
        Map<String, String> result = new HashMap<>();

        if (isRedisAvailable() && redisTemplate != null) {
            try {
                // Use Redis SCAN to get keys matching the pattern
                // Pattern: idempotency:{userId}:*
                String pattern = "idempotency:" + userId + ":*";
                
                // Use Redis keys command (note: use SCAN in production for large datasets)
                java.util.Set<String> keys = redisTemplate.keys(pattern);
                
                if (keys != null) {
                    for (String key : keys) {
                        if (result.size() >= limit) {
                            break;
                        }
                        String idempotencyKey = key.substring(key.lastIndexOf(":") + 1);
                        Object notificationId = redisTemplate.opsForValue().get(key);
                        if (notificationId != null) {
                            result.put(idempotencyKey, notificationId.toString());
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                log.warn("Failed to get recent keys from Redis: {}", e.getMessage());
            }
        }
        
        // Fallback to local cache
        {
            synchronized (localCache) {
                localCache.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith("idempotency:" + userId + ":"))
                        .limit(limit)
                        .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            }
        }

        return result;
    }

    private String buildCacheKey(String idempotencyKey, String userId) {
        return String.format("idempotency:%s:%s", userId, idempotencyKey);
    }

    private void storeInLocalCache(String cacheKey, String notificationId) {
        log.info("storing notification id in local cache");
        synchronized (localCache) {
            localCache.put(cacheKey, notificationId);
            localCacheTimestamps.put(cacheKey, LocalDateTime.now());
            log.info("Stored idempotency key in local cache: {}", cacheKey);
        }
    }

    private void cleanupLocalCache() {
        log.info("cleaning up local cache");
        LocalDateTime cutoff = LocalDateTime.now().minusHours(properties.getTtlHours());

        localCacheTimestamps.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                localCache.remove(entry.getKey());
                return true;
            }
            return false;
        });

        log.info("Cleaned up local idempotency cache, remaining entries: {}", localCache.size());
    }

    private boolean isRedisAvailable() {
        if (redisTemplate == null) {
            return false;
        }
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            log.debug("Redis not available: {}", e.getMessage());
            return false;
        }
    }
}
