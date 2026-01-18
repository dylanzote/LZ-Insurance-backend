package com.zote.common.utils.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.SslOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Configuration for LZ Insurance Microservices
 * 
 * Configures Redis connection factory and template for:
 * - Rate limiting
 * - Idempotency checks
 * - Caching
 * - Distributed locks
 * 
 * Supports Redis Cloud with SSL/TLS connections
 * 
 * Usage: Include 'redis' profile in your service's application.yml
 * 
 * @author LZ Insurance System
 * @since 1.0.0
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.redis.host")
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RedisConfig {

//    @Value("${spring.redis.host:localhost}")
//    private String host;
//
//    @Value("${spring.redis.port:6379}")
//    private int port;
//
//    @Value("${spring.redis.password:}")
//    private String password;
//
//    @Value("${spring.redis.ssl.enabled:false}")
//    private boolean ssl;
//
//    @Value("${spring.redis.username:default}")
//    private String username;
//
//    /**
//     * Creates Redis connection factory
//     * Supports both standard Redis and Redis Cloud (with SSL)
//     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName(host);
//        config.setPort(port);
//
//        if (username != null && !username.isEmpty()) {
//            config.setUsername(username);
//        }
//
//        if (password != null && !password.isEmpty()) {
//            config.setPassword(password);
//        }
//
//        // Configure Lettuce client with SSL support for Redis Cloud
//        LettuceClientConfiguration clientConfig;
//
//        if (ssl) {
//            // Configure SSL for Redis Cloud
//            SslOptions sslOptions = SslOptions.builder().build();
//
//            ClientOptions clientOptions = ClientOptions.builder()
//                    .socketOptions(SocketOptions.builder()
//                            .connectTimeout(Duration.ofSeconds(2))
//                            .build())
//                    .sslOptions(sslOptions)
//                    .build();
//
//            // Create client configuration with SSL
//            clientConfig = LettuceClientConfiguration.builder()
//                    .clientOptions(clientOptions)
//                    .commandTimeout(Duration.ofSeconds(2))
//                    .shutdownTimeout(Duration.ofMillis(200))
//                    .build();
//
//            log.info("SSL enabled for Redis connection");
//        } else {
//            // Standard configuration without SSL
//            clientConfig = LettuceClientConfiguration.builder()
//                    .commandTimeout(Duration.ofSeconds(2))
//                    .shutdownTimeout(Duration.ofMillis(200))
//                    .build();
//        }
//
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
//        factory.setValidateConnection(true);
//
//        log.info("Redis connection factory configured - Host: {}, Port: {}, SSL: {}", host, port, ssl);
//
//        return factory;
//    }
//
//    /**
//     * Creates RedisTemplate for String/Object operations
//     * Used by RateLimiterService, IdempotencyService, and other Redis operations
//     */
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Use String serializer for keys
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // Use JSON serializer for values
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        // Enable transaction support
//        template.setEnableTransactionSupport(false);
//        template.afterPropertiesSet();
//
//        log.info("RedisTemplate configured successfully");
//
//        return template;
//    }
//
//    /**
//     * Creates RedisTemplate specifically for String/String operations
//     * Used by rate limiting services
//     */
//    @Bean
//    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Use String serializer for both keys and values
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//        template.setKeySerializer(stringSerializer);
//        template.setValueSerializer(stringSerializer);
//        template.setHashKeySerializer(stringSerializer);
//        template.setHashValueSerializer(stringSerializer);
//
//        template.afterPropertiesSet();
//
//        return template;
//    }

}
