package com.zote.user.service.domain.support;

import com.zote.common.utils.config.WebConfig;
import com.zote.user.service.domain.model.UserActivity;
import com.zote.user.service.domain.ports.outbound.UserActivityRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogger {

    private final UserActivityRepositoryPort userActivityRepositoryPort;

    private final UserRepositoryPort userRepositoryPort;

    private final WebConfig webConfig;

    public void logActivity(String userId, String action, String resource, String resourceId) {
        try {
            String ipAddress = webConfig.getClientIpAddress();
            
            UserActivity activity = UserActivity.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .action(action)
                    .resource(resource)
                    .resourceId(resourceId)
                    .ipAddress(ipAddress)
                    .build();

            var user = userRepositoryPort.findUserById(userId);
            userActivityRepositoryPort.saveActivity(activity, user);
            log.debug("Activity logged: {} - {} - {}", userId, action, resource);
        } catch (Exception e) {
            log.error("Failed to log activity for user {}: {}", userId, e.getMessage());
        }
    }


    public void logActivityAsync(String userId, String action, String resource, String resourceId) {
        CompletableFuture
                .runAsync(() -> logActivity(userId, action, resource, resourceId))
                .exceptionally(ex -> {
                    log.warn("Failed to log activity for user: {}", userId, ex);
                    return null;
                });
    }
}

