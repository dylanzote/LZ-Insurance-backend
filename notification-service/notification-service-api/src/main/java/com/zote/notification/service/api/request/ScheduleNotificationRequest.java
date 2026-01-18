package com.zote.notification.service.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationType;
import com.zote.notification.service.domain.model.ScheduledNotificationData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ScheduleNotificationRequest {
    private String userId;

    @NotBlank
    private String title;

    private String message;

    private String templateId;

    private Map<String, Object> templateVariables;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationType type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    private String cronExpression;

    public ScheduledNotificationData toScheduleNotificationCommand() {
        var data = new ScheduledNotificationData();
        BeanUtils.copyProperties(this, data);
        return data;
    }
}
