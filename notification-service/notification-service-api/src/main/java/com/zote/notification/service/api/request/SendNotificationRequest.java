package com.zote.notification.service.api.request;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.notification.service.domain.model.CreateProviderData;
import com.zote.notification.service.domain.model.SendNotificationData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SendNotificationRequest {

    @NotBlank
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

    private String idempotencyKey;

    private NotificationPriority priority;

    private String locale;

    private Map<String, Object> metadata;

    private LocalDateTime scheduledAt;

    private Boolean bypassPreferences = false;

    public SendNotificationData toSendNotificationData() {
        var sendNotificationData = new SendNotificationData();
        BeanUtils.copyProperties(this, sendNotificationData);
        return sendNotificationData;
    }
}
