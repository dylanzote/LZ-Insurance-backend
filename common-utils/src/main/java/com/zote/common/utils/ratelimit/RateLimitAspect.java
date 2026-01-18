package com.zote.common.utils.ratelimit;

import com.zote.common.utils.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * AOP Aspect for intercepting @RateLimit annotations
 * 
 * Features:
 * - Intercepts method calls before execution
 * - Generates rate limit key based on strategy
 * - Checks rate limit using RateLimiterService
 * - Throws RateLimitExceededException if limit exceeded
 * - Adds rate limit headers to response
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private final RateLimiterService rateLimiterService;
    
    @Around("@annotation(com.zote.common.utils.ratelimit.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        // Generate rate limit key
        String key = generateKey(rateLimit, method);
        
        // Check rate limit
        boolean allowed = rateLimiterService.isAllowed(
            key,
            rateLimit.limit(),
            rateLimit.window(),
            rateLimit.timeUnit()
        );
        
        // Add rate limit headers to response
        addRateLimitHeaders(key, rateLimit.limit());
        
        if (!allowed) {
            long retryAfter = rateLimiterService.getTimeUntilReset(key);
            log.warn("Rate limit exceeded for key: {} (limit: {}, window: {} {}, retry after: {}s)",
                    key, rateLimit.limit(), rateLimit.window(), rateLimit.timeUnit(), retryAfter);
            
            throw new RateLimitExceededException(
                rateLimit.message(),
                key,
                rateLimit.limit(),
                retryAfter
            );
        }
        
        // Proceed with method execution
        return joinPoint.proceed();
    }
    
    /**
     * Generate rate limit key based on strategy
     */
    private String generateKey(RateLimit rateLimit, Method method) {
        String baseKey = rateLimit.keyPrefix().isEmpty() 
            ? method.getDeclaringClass().getSimpleName() + "." + method.getName()
            : rateLimit.keyPrefix();
        
        return switch (rateLimit.keyStrategy()) {
            case USER -> {
                String userId = getCurrentUserId();
                yield userId != null ? baseKey + ":user:" + userId : baseKey + ":anonymous";
            }
            case IP -> baseKey + ":ip:" + getClientIp();
            case ENDPOINT -> baseKey + ":endpoint";
            case USER_ENDPOINT -> {
                String userId = getCurrentUserId();
                String userPart = userId != null ? userId : "anonymous";
                yield baseKey + ":user:" + userPart + ":endpoint";
            }
            case IP_ENDPOINT -> baseKey + ":ip:" + getClientIp() + ":endpoint";
        };
    }
    
    /**
     * Get current user ID from security context
     */
    private String getCurrentUserId() {
        try {
            return SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Check for proxy headers
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // Get first IP if multiple
                    return ip.split(",")[0].trim();
                }
                
                ip = request.getHeader("X-Real-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
                
                return request.getRemoteAddr();
            }
            return "unknown";
        } catch (Exception e) {
            log.warn("Failed to get client IP", e);
            return "unknown";
        }
    }
    
    /**
     * Add rate limit headers to HTTP response
     */
    private void addRateLimitHeaders(String key, int limit) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                long remaining = rateLimiterService.getRemainingRequests(key, limit);
                long resetTime = rateLimiterService.getTimeUntilReset(key);
                
                attributes.getResponse().setHeader("X-RateLimit-Limit", String.valueOf(limit));
                attributes.getResponse().setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
                attributes.getResponse().setHeader("X-RateLimit-Reset", String.valueOf(resetTime));
            }
        } catch (Exception e) {
            log.debug("Failed to add rate limit headers", e);
        }
    }
}

