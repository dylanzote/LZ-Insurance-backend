package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.NotificationStatusResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class NotificationStatusResponse {
    private String notificationId;
    private String status;
    private String providerMessageId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;

    private String errorMessage;

    public static NotificationStatusResponse toNotificationStatusResponse(NotificationStatusResult result) {
        var response = new NotificationStatusResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }
}
