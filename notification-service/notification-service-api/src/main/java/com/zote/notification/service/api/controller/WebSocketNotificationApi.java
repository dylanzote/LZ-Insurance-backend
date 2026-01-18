package com.zote.notification.service.api.controller;

import com.zote.common.utils.models.ErrorResponse;
import com.zote.notification.service.api.request.MarkReadRequest;
import com.zote.notification.service.api.request.SubscribeRequest;
import com.zote.notification.service.api.request.UnsubscribeRequest;
import com.zote.notification.service.api.response.SubscriptionResponse;
import com.zote.notification.service.domain.ports.inbound.MonitorNotificationsPort;
import com.zote.notification.service.domain.ports.outbound.service.WebSocketServicePort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "WebSocket Notification API")
@Controller
@MessageMapping("/notifications")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationApi {

    private final MonitorNotificationsPort monitorNotificationsPort;

    @MessageMapping("/subscribe")
    @SendToUser("/queue/subscriptions")
    public SubscriptionResponse subscribe(@Payload SubscribeRequest request,
                                         @Header("simpSessionId") String sessionId,
                                         Principal principal) {

        String userId = principal.getName();
        log.info("User {} subscribed to notifications via session {}", userId, sessionId);

        // Note: Session registration and topic management should be handled by WebSocketServicePort
        // For now, return basic subscription response
        return SubscriptionResponse.builder()
            .status("SUBSCRIBED")
            .sessionId(sessionId)
            .timestamp(LocalDateTime.now())
            .topics(List.of()) // Topics should come from WebSocketServicePort if needed
            .build();
    }

    @MessageMapping("/unsubscribe")
    public void unsubscribe(@Payload UnsubscribeRequest request,
                           @Header("simpSessionId") String sessionId,
                           Principal principal) {

        String userId = principal.getName();
        log.info("User {} unsubscribed from notifications", userId);

        // Note: Session unregistration should be handled by WebSocketServicePort
    }

    @MessageMapping("/mark-read")
    public void markAsRead(@Payload MarkReadRequest request,
                          Principal principal) {

        String userId = principal.getName();
        log.info("User {} marked notification {} as read", userId, request.getNotificationId());

        monitorNotificationsPort.markAsRead(request.getNotificationId(), userId);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleException(Exception ex,
                                        @Header("simpSessionId") String sessionId) {
        log.error("WebSocket error for session {}", sessionId, ex);

        return ErrorResponse.builder()
            .error(ex.getClass().getSimpleName())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .sessionId(sessionId)
            .build();
    }
}
