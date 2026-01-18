package com.zote.notification.service.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.notification.service.domain.model.BulkNotificationData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class BulkSendRequest {
    @NotNull
    private List<String> userIds;

    @NotBlank
    private String title;

    private String message;

    private String templateId;

    private Map<String, Object> templateVariables;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationType type;

    private NotificationPriority priority;

    private String locale;

    private Map<String, Object> metadata;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;

    private Boolean bypassPreferences = false;

    public BulkNotificationData toSendBulkNotificationCommand() {
        var data = new BulkNotificationData();
        BeanUtils.copyProperties(this, data);
        return data;
    }
}
