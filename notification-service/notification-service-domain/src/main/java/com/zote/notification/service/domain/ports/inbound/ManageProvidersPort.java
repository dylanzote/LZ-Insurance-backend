package com.zote.notification.service.domain.ports.inbound;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.CreateProviderData;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.UpdateProviderData;

import java.util.List;
import java.util.Map;

public interface ManageProvidersPort {
    NotificationProvider createProvider(CreateProviderData createProviderData);
    NotificationProvider updateProvider(UpdateProviderData data);
    void deleteProvider(String providerId);
    List<NotificationProvider> getAllProviders();
    List<NotificationProvider> getProvidersByChannel(NotificationChannel channel);
    void enableProvider(String providerId);
    void disableProvider(String providerId);
    NotificationProvider updateProviderConfig(String providerId, Map<String, Object> config);
}
