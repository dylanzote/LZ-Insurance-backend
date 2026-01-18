package com.zote.common.utils.ratelimit;

import java.util.concurrent.TimeUnit;

/**
 * Service interface for rate limiting operations
 * 
 * Implementations:
 * - RedisRateLimiterService: Distributed rate limiting using Redis
 * - InMemoryRateLimiterService: Single-instance rate limiting using local cache
 */
public interface RateLimiterService {
    
    /**
     * Check if a request is allowed under the rate limit
     * 
     * @param key Unique identifier for the rate limit (user ID, IP, endpoint, etc.)
     * @param limit Maximum number of requests allowed
     * @param window Time window duration
     * @param timeUnit Time unit for the window
     * @return true if request is allowed, false if rate limit exceeded
     */
    boolean isAllowed(String key, int limit, long window, TimeUnit timeUnit);
    
    /**
     * Get current request count for a key
     * 
     * @param key Unique identifier
     * @return Current request count
     */
    long getCurrentCount(String key);
    
    /**
     * Get remaining requests allowed for a key
     * 
     * @param key Unique identifier
     * @param limit Maximum number of requests allowed
     * @return Remaining requests
     */
    long getRemainingRequests(String key, int limit);
    
    /**
     * Reset rate limit for a key (admin operation)
     * 
     * @param key Unique identifier
     */
    void reset(String key);
    
    /**
     * Get time until rate limit reset (in seconds)
     * 
     * @param key Unique identifier
     * @return Seconds until reset
     */
    long getTimeUntilReset(String key);
}

