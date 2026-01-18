package com.zote.common.utils.ratelimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to enable rate limiting on REST API endpoints
 * 
 * Usage:
 * <pre>
 * {@literal @}RateLimit(limit = 10, window = 1, timeUnit = TimeUnit.MINUTES)
 * public ResponseEntity<?> someEndpoint() {
 *     // Your code here
 * }
 * </pre>
 * 
 * Features:
 * - User-based rate limiting (authenticated users)
 * - IP-based rate limiting (anonymous requests)
 * - Endpoint-specific rate limiting
 * - Configurable time windows
 * - Redis-based for distributed systems
 * - In-memory fallback for single instance
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * Maximum number of requests allowed in the time window
     * Default: 100 requests
     */
    int limit() default 100;
    
    /**
     * Time window duration
     * Default: 1 minute
     */
    int window() default 1;
    
    /**
     * Time unit for the window
     * Default: MINUTES
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    
    /**
     * Rate limit key strategy
     * Default: USER (rate limit per authenticated user)
     */
    RateLimitKeyStrategy keyStrategy() default RateLimitKeyStrategy.USER;
    
    /**
     * Custom key prefix (optional)
     * Useful for grouping related endpoints
     */
    String keyPrefix() default "";
    
    /**
     * Error message when rate limit is exceeded
     */
    String message() default "Rate limit exceeded. Please try again later.";
}

