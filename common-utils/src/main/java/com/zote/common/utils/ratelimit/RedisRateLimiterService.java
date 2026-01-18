package com.zote.common.utils.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based distributed rate limiter using sliding window algorithm
 * 
 * Features:
 * - Distributed across multiple service instances
 * - Sliding window for accurate rate limiting
 * - Atomic operations using Lua scripts
 * - Automatic expiry of old data
 * 
 * Activation:
 * - Only created when RedisTemplate bean is available
 * - Requires 'redis' profile to be active
 * - Takes priority over InMemoryRateLimiterService when available
 */
@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnBean(RedisTemplate.class)
public class RedisRateLimiterService implements RateLimiterService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";
    
    /**
     * Lua script for atomic sliding window rate limiting
     * Returns: 1 if allowed, 0 if rate limit exceeded
     */
    private static final String LUA_SCRIPT = 
        "local key = KEYS[1] " +
        "local limit = tonumber(ARGV[1]) " +
        "local window = tonumber(ARGV[2]) " +
        "local current_time = tonumber(ARGV[3]) " +
        "local window_start = current_time - window " +
        "" +
        "redis.call('ZREMRANGEBYSCORE', key, 0, window_start) " +
        "local current_count = redis.call('ZCARD', key) " +
        "" +
        "if current_count < limit then " +
        "  redis.call('ZADD', key, current_time, current_time) " +
        "  redis.call('EXPIRE', key, window) " +
        "  return 1 " +
        "else " +
        "  return 0 " +
        "end";
    
    @Override
    public boolean isAllowed(String key, int limit, long window, TimeUnit timeUnit) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            long windowSeconds = timeUnit.toSeconds(window);
            long currentTime = Instant.now().getEpochSecond();
            
            RedisScript<Long> script = RedisScript.of(LUA_SCRIPT, Long.class);
            Long result = redisTemplate.execute(
                script,
                Collections.singletonList(redisKey),
                String.valueOf(limit),
                String.valueOf(windowSeconds),
                String.valueOf(currentTime)
            );
            
            boolean allowed = result != null && result == 1L;
            
            if (!allowed) {
                log.warn("Rate limit exceeded for key: {} (limit: {}, window: {}s)", 
                        key, limit, windowSeconds);
            }
            
            return allowed;
            
        } catch (Exception e) {
            log.error("Error checking rate limit for key: {}", key, e);
            // Fail open: allow request if Redis is down
            return true;
        }
    }
    
    @Override
    public long getCurrentCount(String key) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            Long count = redisTemplate.opsForZSet().zCard(redisKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Error getting current count for key: {}", key, e);
            return 0;
        }
    }
    
    @Override
    public long getRemainingRequests(String key, int limit) {
        long current = getCurrentCount(key);
        return Math.max(0, limit - current);
    }
    
    @Override
    public void reset(String key) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            redisTemplate.delete(redisKey);
            log.info("Rate limit reset for key: {}", key);
        } catch (Exception e) {
            log.error("Error resetting rate limit for key: {}", key, e);
        }
    }
    
    @Override
    public long getTimeUntilReset(String key) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? ttl : 0;
        } catch (Exception e) {
            log.error("Error getting TTL for key: {}", key, e);
            return 0;
        }
    }
}

