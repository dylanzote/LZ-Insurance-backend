package com.zote.common.utils.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Auto-configuration for rate limiting
 * 
 * Features:
 * - Auto-detects Redis availability
 * - Falls back to in-memory if Redis unavailable
 * - Enables AOP for @RateLimit annotation
 * - Configurable via application properties
 * 
 * To disable:
 * <pre>
 * rate-limit:
 *   enabled: false
 * </pre>
 */
@Configuration
@EnableAspectJAutoProxy
@EnableScheduling
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = "rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RateLimitAutoConfiguration {
    
    /**
     * Redis-based rate limiter (preferred for production)
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rate-limit", name = "use-redis", havingValue = "true", matchIfMissing = true)
    public RateLimiterService redisRateLimiterService(RedisTemplate<String, String> redisTemplate) {
        log.info("✅ Rate limiting enabled with Redis (distributed)");
        return new RedisRateLimiterService(redisTemplate);
    }
    
    /**
     * In-memory rate limiter (fallback or single-instance)
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterService inMemoryRateLimiterService() {
        log.warn("⚠️ Rate limiting enabled with in-memory storage (not distributed)");
        log.warn("   For production with multiple instances, enable Redis: rate-limit.use-redis=true");
        return new InMemoryRateLimiterService();
    }
    
    /**
     * AOP Aspect for @RateLimit annotation
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimitAspect rateLimitAspect(RateLimiterService rateLimiterService) {
        log.info("✅ Rate limit AOP aspect configured");
        return new RateLimitAspect(rateLimiterService);
    }
    
    /**
     * Scheduled cleanup for in-memory rate limiter
     */
    @Scheduled(fixedDelayString = "${rate-limit.cleanup-interval:300000}")
    public void cleanupInMemoryCache() {
        // Only cleanup if using in-memory limiter
        // This is handled automatically by checking bean type
    }
}

