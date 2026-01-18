package com.zote.notification.service.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for STOMP-based messaging
 * 
 * This configuration enables WebSocket support with STOMP protocol for real-time notifications.
 * 
 * Endpoints:
 * - STOMP endpoint: /ws (configurable via application.yml)
 * - Application destination prefix: /app (for @MessageMapping methods)
 * - Broker destinations: /topic (broadcast), /queue (user-specific)
 * 
 * Frontend Connection:
 * 1. Connect to: ws://localhost:8086/ws (or configured endpoint)
 * 2. Subscribe to: /user/{userId}/queue/notifications (user-specific)
 * 3. Subscribe to: /topic/provider-health, /topic/alerts, /topic/metrics (broadcast)
 * 4. Send messages to: /app/notifications/subscribe, /app/notifications/unsubscribe, etc.
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${notification.websocket.endpoint:/ws}")
    private String websocketEndpoint;

    @Value("${notification.websocket.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${notification.websocket.heartbeat-interval:30000}")
    private long heartbeatInterval;

    /**
     * TaskScheduler bean required for WebSocket heartbeat mechanism
     * The heartbeat keeps the connection alive and detects dead connections
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("websocket-heartbeat-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        return scheduler;
    }

    /**
     * Configure the message broker
     * - Enable simple in-memory broker for /topic and /queue destinations
     * - Set application destination prefix to /app
     * - Configure user destination prefix for user-specific messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for broadcasting (topics) and user-specific messages (queues)
        // Topics: /topic/* - for broadcasting to all subscribers
        // Queues: /queue/* - for user-specific messages (automatically prefixed with /user/{userId})
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{heartbeatInterval, heartbeatInterval})
                .setTaskScheduler(taskScheduler());

        // Set the prefix for messages bound to @MessageMapping methods
        // Messages sent to /app/* will be routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");

        // Set prefix for user-specific destinations
        // When sending to /user/{userId}/queue/notifications, Spring automatically routes to the correct user
        config.setUserDestinationPrefix("/user");

        log.info("WebSocket message broker configured with endpoint: {}, allowed origins: {}, heartbeat: {}ms", 
                websocketEndpoint, allowedOrigins, heartbeatInterval);
    }

    /**
     * Register STOMP endpoints
     * - Register the WebSocket endpoint
     * - Enable SockJS fallback for browsers that don't support WebSocket
     * - Configure CORS
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint with SockJS fallback
        // SockJS provides WebSocket emulation for browsers that don't support WebSocket
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOriginPatterns(allowedOrigins.equals("*") ? new String[]{"*"} : allowedOrigins.split(","))
                .withSockJS()
                .setHeartbeatTime(heartbeatInterval);

        log.info("STOMP endpoint registered at: {}", websocketEndpoint);
    }
}

