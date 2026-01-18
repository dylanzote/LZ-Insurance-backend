package com.zote.common.utils.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for rate limiting
 * 
 * Usage in application.yml:
 * <pre>
 * rate-limit:
 *   enabled: true
 *   use-redis: true
 *   cleanup-interval: 300000  # 5 minutes
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
    
    /**
     * Enable/disable rate limiting globally
     * Default: true
     */
    private boolean enabled = true;
    
    /**
     * Use Redis for distributed rate limiting
     * If false, uses in-memory rate limiter
     * Default: true (auto-detected based on Redis availability)
     */
    private Boolean useRedis;
    
    /**
     * Cleanup interval for in-memory rate limiter (milliseconds)
     * Default: 5 minutes
     */
    private long cleanupInterval = 300000;
    
    /**
     * Fail open if rate limiter throws exception
     * true = allow request if error occurs
     * false = deny request if error occurs
     * Default: true (fail open for resilience)
     */
    private boolean failOpen = true;
}

