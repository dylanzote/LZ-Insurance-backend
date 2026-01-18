package com.zote.notification.service.infrastructure.service;

import com.zote.common.utils.config.RateLimiterProperties;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.ports.outbound.service.RateLimiterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterService implements RateLimiterPort {

    // Optional Redis support - injected only if Redis is available
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    private final RateLimiterProperties rateLimiterProperties;

    // In-memory cache for local rate limiting (for single instance)
    private final Map<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean acquireToken(String userId, NotificationChannel channel) {
        String key = String.format("rate_limit:%s:%s", userId, channel.name().toLowerCase());
        return acquireTokenInternal(key, getChannelLimit(channel));
    }

    @Override
    public boolean acquireToken(String userId, String providerId) {
        String key = String.format("rate_limit:%s:provider:%s", userId, providerId);
        return acquireTokenInternal(key, getProviderLimit(providerId));
    }

    @Override
    public void resetBucket(String userId, NotificationChannel channel) {
        String key = String.format("rate_limit:%s:%s", userId, channel.name().toLowerCase());
        resetBucketInternal(key);
    }

    @Override
    public Map<String, Long> getRateLimitStats(String userId) {
        Map<String, Long> stats = new HashMap<>();

        for (NotificationChannel channel : NotificationChannel.values()) {
            String key = String.format("rate_limit:%s:%s", userId, channel.name().toLowerCase());
            Long remaining = getRemainingTokens(key);
            stats.put(channel.name().toLowerCase(), remaining != null ? remaining : getChannelLimit(channel));
        }

        return stats;
    }

    @Override
    public Map<String, Long> getProviderRateLimitStats(String providerId) {
        Map<String, Long> stats = new HashMap<>();
        String key = String.format("rate_limit:provider:%s", providerId);
        Long remaining = getRemainingTokens(key);
        stats.put(providerId, remaining != null ? remaining : getProviderLimit(providerId));
        return stats;
    }

    // Private helper methods
    private boolean acquireTokenInternal(String key, long limit) {
        // Try Redis-based rate limiting first
        if (isRedisAvailable()) {
            return acquireTokenFromRedis(key, limit);
        }

        // Fallback to local rate limiting
        return acquireTokenLocally(key, limit);
    }

    private boolean acquireTokenFromRedis(String key, long limit) {
        try {
            if (redisTemplate == null) {
                return acquireTokenLocally(key, limit);
            }

            // Using Redis sorted set for sliding window rate limiting
            long now = System.currentTimeMillis();
            long windowSize = 60_000; // 1 minute window

            // Remove old entries outside the window
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, now - windowSize);

            // Count requests in current window
            Long count = redisTemplate.opsForZSet().count(key, now - windowSize, now);

            if (count != null && count >= limit) {
                log.debug("Rate limit exceeded for key: {}, count: {}, limit: {}", key, count, limit);
                return false;
            }

            // Add current request with current timestamp as score
            redisTemplate.opsForZSet().add(key, String.valueOf(now), now);
            redisTemplate.expire(key, Duration.ofMinutes(2)); // Expire after 2 minutes

            return true;

        } catch (Exception e) {
            log.warn("Redis rate limiting failed, falling back to local: {}", e.getMessage());
            return acquireTokenLocally(key, limit);
        }
    }

    private boolean acquireTokenLocally(String key, long limit) {
        TokenBucket bucket = tokenBuckets.computeIfAbsent(key,
                k -> new TokenBucket(limit, 60_000)); // Refill 1 token per minute

        return bucket.tryAcquire();
    }

    private void resetBucketInternal(String key) {
        // Clear Redis key
        if (isRedisAvailable() && redisTemplate != null) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.warn("Failed to delete Redis key {}: {}", key, e.getMessage());
            }
        }

        // Clear local bucket
        tokenBuckets.remove(key);
    }

    private Long getRemainingTokens(String key) {
        if (isRedisAvailable() && redisTemplate != null) {
            try {
                long now = System.currentTimeMillis();
                long windowSize = 60_000;

                // Remove old entries
                redisTemplate.opsForZSet().removeRangeByScore(key, 0, now - windowSize);

                // Count requests in current window
                Long count = redisTemplate.opsForZSet().count(key, now - windowSize, now);

                if (count != null) {
                    long limit = getChannelLimitFromKey(key);
                    return Math.max(0, limit - count);
                }
            } catch (Exception e) {
                log.warn("Failed to get remaining tokens from Redis: {}", e.getMessage());
            }
        }

        // Check local bucket
        TokenBucket bucket = tokenBuckets.get(key);
        return bucket != null ? bucket.getRemainingTokens() : null;
    }

    private long getChannelLimit(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> rateLimiterProperties.getEmail().getMaxPerMinute();
            case SMS -> rateLimiterProperties.getSms().getMaxPerMinute();
            case PUSH -> rateLimiterProperties.getPush().getMaxPerMinute();
            case WEBHOOK -> rateLimiterProperties.getWebhook().getMaxPerMinute();
            default -> rateLimiterProperties.getDefaultMaxPerMinute();
        };
    }

    private long getChannelLimitFromKey(String key) {
        if (key.contains(":email")) {
            return rateLimiterProperties.getEmail().getMaxPerMinute();
        } else if (key.contains(":sms")) {
            return rateLimiterProperties.getSms().getMaxPerMinute();
        } else if (key.contains(":push")) {
            return rateLimiterProperties.getPush().getMaxPerMinute();
        } else if (key.contains(":webhook")) {
            return rateLimiterProperties.getWebhook().getMaxPerMinute();
        }
        return rateLimiterProperties.getDefaultMaxPerMinute();
    }

    private long getProviderLimit(String providerId) {
        // This would come from provider configuration
        // For now, return default
        return rateLimiterProperties.getDefaultMaxPerMinute();
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

    // Token bucket implementation for local rate limiting
    private static class TokenBucket {
        private final long capacity;
        private final long refillPeriod; // in milliseconds
        private long tokens;
        private long lastRefillTimestamp;

        public TokenBucket(long capacity, long refillPeriod) {
            this.capacity = capacity;
            this.refillPeriod = refillPeriod;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }

            return false;
        }

        public synchronized long getRemainingTokens() {
            refill();
            return tokens;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTimestamp;

            if (timePassed > refillPeriod) {
                long tokensToAdd = timePassed / refillPeriod;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = now - (timePassed % refillPeriod);
            }
        }
    }
}
