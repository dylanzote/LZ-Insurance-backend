package com.zote.common.utils.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * In-memory rate limiter using sliding window algorithm
 * 
 * Features:
 * - No external dependencies (Redis not required)
 * - Sliding window for accurate rate limiting
 * - Thread-safe concurrent operations
 * - Automatic cleanup of old entries
 * 
 * Limitations:
 * - Not distributed (per-instance rate limiting)
 * - Lost on service restart
 * - Higher memory usage for high-traffic scenarios
 * 
 * Use case:
 * - Development/testing environments
 * - Single-instance deployments
 * - Fallback when Redis is unavailable
 * 
 * Activation:
 * - Always available as fallback
 * - Used when Redis is not available
 * - RedisRateLimiterService takes priority when Redis is present
 */
@Service
@Slf4j
public class InMemoryRateLimiterService implements RateLimiterService {
    
    private final Map<String, ConcurrentLinkedDeque<Long>> requestTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Long> windowSizes = new ConcurrentHashMap<>();
    
    @Override
    public boolean isAllowed(String key, int limit, long window, TimeUnit timeUnit) {
        long windowMillis = timeUnit.toMillis(window);
        long currentTime = Instant.now().toEpochMilli();
        long windowStart = currentTime - windowMillis;
        
        // Get or create deque for this key
        ConcurrentLinkedDeque<Long> timestamps = requestTimestamps.computeIfAbsent(
            key, 
            k -> new ConcurrentLinkedDeque<>()
        );
        
        // Store window size for expiry
        windowSizes.put(key, windowMillis);
        
        // Synchronized block to ensure atomic read-modify-write
        synchronized (timestamps) {
            // Remove expired timestamps
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }
            
            // Check if limit exceeded
            if (timestamps.size() >= limit) {
                log.warn("Rate limit exceeded for key: {} (limit: {}, window: {}ms)", 
                        key, limit, windowMillis);
                return false;
            }
            
            // Add current timestamp
            timestamps.addLast(currentTime);
            return true;
        }
    }
    
    @Override
    public long getCurrentCount(String key) {
        ConcurrentLinkedDeque<Long> timestamps = requestTimestamps.get(key);
        if (timestamps == null) {
            return 0;
        }
        
        // Clean expired entries first
        Long windowMillis = windowSizes.get(key);
        if (windowMillis != null) {
            long currentTime = Instant.now().toEpochMilli();
            long windowStart = currentTime - windowMillis;
            
            synchronized (timestamps) {
                while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                    timestamps.pollFirst();
                }
                return timestamps.size();
            }
        }
        
        return timestamps.size();
    }
    
    @Override
    public long getRemainingRequests(String key, int limit) {
        long current = getCurrentCount(key);
        return Math.max(0, limit - current);
    }
    
    @Override
    public void reset(String key) {
        requestTimestamps.remove(key);
        windowSizes.remove(key);
        log.info("Rate limit reset for key: {}", key);
    }
    
    @Override
    public long getTimeUntilReset(String key) {
        ConcurrentLinkedDeque<Long> timestamps = requestTimestamps.get(key);
        Long windowMillis = windowSizes.get(key);
        
        if (timestamps == null || timestamps.isEmpty() || windowMillis == null) {
            return 0;
        }
        
        long oldestTimestamp = timestamps.peekFirst();
        long currentTime = Instant.now().toEpochMilli();
        long resetTime = oldestTimestamp + windowMillis;
        
        return Math.max(0, (resetTime - currentTime) / 1000);
    }
    
    /**
     * Cleanup method to remove old entries
     * Should be called periodically (e.g., via @Scheduled)
     */
    public void cleanup() {
        long currentTime = Instant.now().toEpochMilli();
        int cleanedKeys = 0;
        
        for (Map.Entry<String, ConcurrentLinkedDeque<Long>> entry : requestTimestamps.entrySet()) {
            String key = entry.getKey();
            ConcurrentLinkedDeque<Long> timestamps = entry.getValue();
            Long windowMillis = windowSizes.get(key);
            
            if (windowMillis != null) {
                long windowStart = currentTime - windowMillis;
                
                synchronized (timestamps) {
                    while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                        timestamps.pollFirst();
                    }
                    
                    // Remove empty entries
                    if (timestamps.isEmpty()) {
                        requestTimestamps.remove(key);
                        windowSizes.remove(key);
                        cleanedKeys++;
                    }
                }
            }
        }
        
        if (cleanedKeys > 0) {
            log.debug("Cleaned up {} expired rate limit keys", cleanedKeys);
        }
    }
}

