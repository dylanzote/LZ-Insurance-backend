package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.ProviderHealth;

import java.util.List;

public interface ProviderSelectorPort {
    NotificationProvider selectProvider(NotificationChannel channel);
    List<NotificationProvider> getFallbackProviders(NotificationProvider primaryProvider);
    void updateProviderHealth(String providerId, boolean success, long latencyMs);
    boolean isProviderHealthy(String providerId);
    List<ProviderHealth> getAllProviderHealth();
}
