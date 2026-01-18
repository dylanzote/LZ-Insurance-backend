package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusMessage {
    private String notificationId;
    private NotificationStatus status;
    private LocalDateTime timestamp;
    private String providerMessageId;
    private String error;
}
