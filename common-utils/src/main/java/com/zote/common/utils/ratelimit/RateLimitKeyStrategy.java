package com.zote.common.utils.ratelimit;

/**
 * Strategy for generating rate limit keys
 */
public enum RateLimitKeyStrategy {
    
    /**
     * Rate limit per authenticated user
     * Key: userId
     * Use case: Authenticated API endpoints
     */
    USER,
    
    /**
     * Rate limit per IP address
     * Key: IP address
     * Use case: Public endpoints, anonymous requests
     */
    IP,
    
    /**
     * Rate limit per endpoint
     * Key: endpoint path
     * Use case: Global endpoint throttling
     */
    ENDPOINT,
    
    /**
     * Rate limit per user + endpoint combination
     * Key: userId:endpoint
     * Use case: Fine-grained control per user per endpoint
     */
    USER_ENDPOINT,
    
    /**
     * Rate limit per IP + endpoint combination
     * Key: IP:endpoint
     * Use case: Public endpoints with per-endpoint limits
     */
    IP_ENDPOINT
}

