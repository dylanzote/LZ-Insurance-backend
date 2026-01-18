package com.zote.notification.service.api.usecase;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.controller.NotificationApi;
import com.zote.notification.service.api.request.BulkSendRequest;
import com.zote.notification.service.api.request.ScheduleNotificationRequest;
import com.zote.notification.service.api.request.SendNotificationRequest;
import com.zote.notification.service.api.response.BulkSendResponse;
import com.zote.notification.service.api.response.NotificationResponse;
import com.zote.notification.service.api.response.NotificationStatusResponse;
import com.zote.notification.service.api.response.UnreadCountResponse;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.BulkNotificationData;
import com.zote.notification.service.domain.model.SendNotificationData;
import com.zote.notification.service.domain.model.ScheduledNotificationData;
import com.zote.notification.service.domain.model.UserInfo;
import com.zote.notification.service.domain.ports.inbound.MonitorNotificationsPort;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.usecases.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationService implements NotificationApi {

    private final SendNotificationPort sendNotificationPort;
    private final MonitorNotificationsPort monitorNotificationsPort;
    private final UserValidationService userValidationService;

    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("API: Sending notification for user: {}", request.getUserId());

        UserInfo userInfo = userValidationService.validateAndGetUser(request.getUserId());
        enrichRequestWithUserInfo(request, userInfo);
        var notification = sendNotificationPort.sendNotification(request.toSendNotificationData());
        return NotificationResponse.toResponse(notification);
    }

    @Override
    public BulkSendResponse sendBulkNotifications(BulkSendRequest request) {
        log.info("API: Sending bulk notifications to {} users", request.getUserIds().size());
        
        var result = sendNotificationPort.sendBulkNotifications(request.toSendBulkNotificationCommand());
        return BulkSendResponse.toBulkSendResponse(result);
    }

    @Override
    public NotificationResponse scheduleNotification(ScheduleNotificationRequest request) {
        log.info("API: Scheduling notification for user: {}", request.getUserId());
        
        // Validate user exists and enrich request with user info
        UserInfo userInfo = userValidationService.validateAndGetUser(request.getUserId());
        
        // Enrich notification metadata with user information if not present
        enrichScheduleRequestWithUserInfo(request, userInfo);
        
        var notification = sendNotificationPort.scheduleNotification(request.toScheduleNotificationCommand());
        return NotificationResponse.toResponse(notification);
    }

    @Override
    public NotificationStatusResponse getNotificationStatus(String notificationId) {
        log.info("API: Getting status for notification: {}", notificationId);
        var result = monitorNotificationsPort.getNotificationStatusResult(notificationId);
        return NotificationStatusResponse.toNotificationStatusResponse(result);
    }

    @Override
    public PageResponse getUserNotifications(String userId, int page, int size, NotificationChannel channel, NotificationStatus status) {
        log.info("API: Getting notifications for user: {}, page: {}, size: {}, channel: {}, status: {}", userId, page, size, channel, status);

        Page<Notification> notificationPage = monitorNotificationsPort.getUserNotifications(userId, page, size, channel, status);

        List<Object> content = notificationPage.getContent().stream()
            .map(NotificationResponse::toResponse)
            .map(notificationResponse -> (Object) notificationResponse)
            .toList();

        // Create PageResponse using constructor
        org.springframework.data.domain.Page<Object> objectPage = 
            new org.springframework.data.domain.PageImpl<>(content, notificationPage.getPageable(), notificationPage.getTotalElements());

        return new PageResponse(objectPage);
    }

    @Override
    public UnreadCountResponse getUnreadCount(String userId) {
        log.info("API: Getting unread count for user: {}", userId);
        
        var result = monitorNotificationsPort.getUnreadCount(userId);
        return UnreadCountResponse.toUnreadCountResponse(result);
    }

    @Override
    public void markAsRead(String notificationId) {
        log.info("API: Marking notification {} as read", notificationId);
        
        var notification = monitorNotificationsPort.getNotificationStatus(notificationId);
        monitorNotificationsPort.markAsRead(notificationId, notification.getUserId());
    }

    @Override
    public void markAllAsRead(String userId) {
        log.info("API: Marking all notifications as read for user: {}", userId);
        
        monitorNotificationsPort.markAllAsRead(userId);
    }

    @Override
    public void cancelScheduledNotification(String notificationId) {
        log.info("API: Cancelling scheduled notification: {}", notificationId);
        sendNotificationPort.cancelScheduledNotification(notificationId);
    }

    /**
     * Enrich notification request with user information
     * Adds email, locale, and other user data to metadata if not present
     */
    private void enrichRequestWithUserInfo(SendNotificationRequest request, UserInfo userInfo) {
        if (request.getMetadata() == null) {
            request.setMetadata(new java.util.HashMap<>());
        }

        // Add email if not present
        if (!request.getMetadata().containsKey("email") && userInfo.getEmail() != null) {
            request.getMetadata().put("email", userInfo.getEmail());
            log.debug("Enriched request with user email: {}", userInfo.getEmail());
        }

        // Add locale if not present
        if (request.getLocale() == null && userInfo.getLocale() != null) {
            request.setLocale(userInfo.getLocale());
            log.debug("Enriched request with user locale: {}", userInfo.getLocale());
        }


        // Add user name for personalization
        if (userInfo.getFirstName() != null || userInfo.getLastName() != null) {
            String fullName = (userInfo.getFirstName() != null ? userInfo.getFirstName() : "") +
                            (userInfo.getLastName() != null ? " " + userInfo.getLastName() : "").trim();
            if (!fullName.isEmpty()) {
                request.getMetadata().put("userName", fullName);
            }
        }
    }

    /**
     * Enrich schedule notification request with user information
     */
    private void enrichScheduleRequestWithUserInfo(ScheduleNotificationRequest request, UserInfo userInfo) {
        // For scheduled notifications, we mainly validate user exists
        // User info will be fetched when the notification is actually sent
        log.debug("User validated for scheduled notification: {}", request.getUserId());
    }
}