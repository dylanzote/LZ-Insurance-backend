package com.zote.notification.service.infrastructure.adapters.providers;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.common.utils.exceptions.ProviderNotConfiguredException;
import com.zote.common.utils.exceptions.ProviderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProviderFactory {

    private final Map<String, NotificationProviderAdapter> adapters;

    @Autowired
    public ProviderFactory(List<NotificationProviderAdapter> adapterList) {
        adapters = adapterList.stream()
                .collect(Collectors.toMap(
                        adapter -> adapter.getType().name().toLowerCase() + "-" + adapter.getChannel().name().toLowerCase(),
                        Function.identity()
                ));
    }

    public NotificationProviderAdapter getAdapter(ProviderType type, NotificationChannel channel) {
        String key = type.name().toLowerCase() + "-" + channel.name().toLowerCase();
        NotificationProviderAdapter adapter = adapters.get(key);

        if (adapter == null) {
            throw new ProviderNotFoundException("No adapter found for type: " + type + ", channel: " + channel);
        }

        if (!adapter.isConfigured()) {
            throw new ProviderNotConfiguredException("Provider not configured: " + adapter.getName());
        }

        return adapter;
    }

    public List<NotificationProviderAdapter> getAdaptersForChannel(NotificationChannel channel) {
        return adapters.values().stream()
                .filter(adapter -> adapter.getChannel() == channel)
                .filter(NotificationProviderAdapter::isConfigured)
                .collect(Collectors.toList());
    }
}
