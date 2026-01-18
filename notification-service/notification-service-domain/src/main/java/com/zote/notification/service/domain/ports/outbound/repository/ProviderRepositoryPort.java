package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.ProviderHealthMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProviderRepositoryPort {

    NotificationProvider save(NotificationProvider provider);
    List<NotificationProvider> findByChannel(NotificationChannel channel);
    List<NotificationProvider> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive);
    NotificationProvider findById(String id);
    void deleteById(String id);

    List<NotificationProvider> findAll();
    NotificationProvider findByName(String name);
    void updateProviderHealth(String providerId, boolean success, long latencyMs);
    List<NotificationProvider> findByChannelOrderByPriority(NotificationChannel channel);

    ProviderHealthMetrics saveHealthMetrics(ProviderHealthMetrics metrics);
    Optional<ProviderHealthMetrics> findLatestHealthMetrics(String providerId);
    List<ProviderHealthMetrics> findHealthMetrics(String providerId, LocalDateTime from, LocalDateTime to);
}
