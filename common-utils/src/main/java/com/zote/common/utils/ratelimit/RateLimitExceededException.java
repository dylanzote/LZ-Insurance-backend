package com.zote.common.utils.ratelimit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when rate limit is exceeded
 * Returns HTTP 429 (Too Many Requests)
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    
    private final String key;
    private final int limit;
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String message, String key, int limit, long retryAfterSeconds) {
        super(message);
        this.key = key;
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public String getKey() {
        return key;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

