package com.zote.notification.service.api.controller;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.request.BulkSendRequest;
import com.zote.notification.service.api.request.ScheduleNotificationRequest;
import com.zote.notification.service.api.request.SendNotificationRequest;
import com.zote.notification.service.api.response.BulkSendResponse;
import com.zote.notification.service.api.response.NotificationResponse;
import com.zote.notification.service.api.response.NotificationStatusResponse;
import com.zote.notification.service.api.response.UnreadCountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification API")
@RequestMapping("/notification/notifications")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface NotificationApi {

    @Operation(summary = "Send a notification")
    @PostMapping("/send")
    NotificationResponse sendNotification(@Valid @RequestBody SendNotificationRequest request);

    @Operation(summary = "Send bulk notifications")
    @PostMapping("/send/bulk")
    BulkSendResponse sendBulkNotifications(@Valid @RequestBody BulkSendRequest request);

    @Operation(summary = "Schedule a notification")
    @PostMapping("/schedule")
    NotificationResponse scheduleNotification(@Valid @RequestBody ScheduleNotificationRequest request);

    @Operation(summary = "Get notification status")
    @GetMapping("/{notificationId}/status")
    NotificationStatusResponse getNotificationStatus(@PathVariable("notificationId") String notificationId);

    @Operation(summary = "Get unread count")
    @GetMapping("/user/{userId}/unread-count")
    UnreadCountResponse getUnreadCount(@PathVariable("userId")  String userId);

    @Operation(summary = "Get user notifications")
    @GetMapping("/user/{userId}")
    PageResponse getUserNotifications(
            @PathVariable("userId")  String userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "channel", required = false) NotificationChannel channel,
            @RequestParam(name = "status", required = false) NotificationStatus status);

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{notificationId}/read")
    void markAsRead(@PathVariable("notificationId") String notificationId);

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/user/{userId}/read-all")
    void markAllAsRead(@PathVariable("userId")  String userId);

    @Operation(summary = "Cancel scheduled notification")
    @DeleteMapping("/{notificationId}")
    void cancelScheduledNotification(@PathVariable("notificationId")  String notificationId);
}
