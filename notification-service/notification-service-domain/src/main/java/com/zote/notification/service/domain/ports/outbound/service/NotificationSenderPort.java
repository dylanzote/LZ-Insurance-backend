package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;

public interface NotificationSenderPort {
    NotificationResult send(Notification notification, ProviderType providerType, NotificationChannel channel);
}
