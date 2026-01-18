package com.zote.notification.service.infrastructure.adapters.providers;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;

import java.util.Map;

public interface NotificationProviderAdapter {
    String getName();

    ProviderType getType();

    NotificationChannel getChannel();

    boolean isConfigured();

    NotificationResult send(Notification notification);

    ProviderHealth healthCheck();

    void configure(Map<String, Object> config);
}
