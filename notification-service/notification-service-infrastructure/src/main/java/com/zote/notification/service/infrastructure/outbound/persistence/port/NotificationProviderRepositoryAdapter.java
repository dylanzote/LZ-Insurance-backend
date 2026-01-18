package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.ProviderHealthMetrics;
import com.zote.notification.service.domain.ports.outbound.repository.ProviderRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationProviderEntity;
import com.zote.notification.service.infrastructure.outbound.entities.ProviderHealthMetricsEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.NotificationProviderRepository;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.ProviderHealthMetricsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProviderRepositoryAdapter implements ProviderRepositoryPort {

    private final NotificationProviderRepository providerRepository;
    private final ProviderHealthMetricsRepository healthMetricsRepository;

    @Override
    public NotificationProvider save(NotificationProvider provider) {
        log.info("saving notification provider: {}", provider);
        return providerRepository.save(NotificationProviderEntity.toEntity(provider)).toDto();
    }

    @Override
    public List<NotificationProvider> findByChannel(NotificationChannel channel) {
        log.info("finding notification providers with channel: {}", channel);
        return providerRepository.findByChannel(channel).stream()
                .map(NotificationProviderEntity::toDto)
                .toList();
    }

    @Override
    public List<NotificationProvider> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive) {
        log.info("finding notification providers with channel: {} and isActive: {}", channel, isActive);
        return providerRepository.findByChannelAndIsActive(channel, isActive).stream()
                .map(NotificationProviderEntity::toDto)
                .toList();
    }

    @Override
    public NotificationProvider findById(String id) {
        log.info("finding notification provider with id: {}", id);
        return providerRepository.findById(id)
                .map(NotificationProviderEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Notification provider not found with id: " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("deleting notification provider with id: {}", id);
        providerRepository.deleteById(id);
    }

    @Override
    public List<NotificationProvider> findAll() {
        log.info("finding all notification providers");
        return providerRepository.findAll().stream()
                .map(NotificationProviderEntity::toDto)
                .toList();
    }

    @Override
    public NotificationProvider findByName(String name) {
        log.info("finding notification provider with name: {}", name);
        return providerRepository.findByName(name)
                .map(NotificationProviderEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Notification provider not found with name: " + name));
    }

    @Override
    @Transactional
    public void updateProviderHealth(String providerId, boolean success, long latencyMs) {
        // Get or create health metrics
        ProviderHealthMetricsEntity metrics = healthMetricsRepository.findTopByProviderIdOrderByTimestampDesc(providerId)
                .orElse(ProviderHealthMetricsEntity.builder()
                        .providerId(providerId)
                        .timestamp(LocalDateTime.now())
                        .successCount(0)
                        .failureCount(0)
                        .totalRequests(0)
                        .averageLatencyMs(0)
                        .errorRatePercent(0.0)
                        .build());

        // Update metrics
        metrics.setTimestamp(LocalDateTime.now());
        metrics.setTotalRequests(metrics.getTotalRequests() + 1);

        if (success) {
            metrics.setSuccessCount(metrics.getSuccessCount() + 1);
            metrics.setLastSuccessAt(LocalDateTime.now());

            // Update average latency (moving average)
            if (metrics.getAverageLatencyMs() == null) {
                metrics.setAverageLatencyMs((int) latencyMs);
            } else {
                metrics.setAverageLatencyMs((int) (
                        (metrics.getAverageLatencyMs() * 0.7) + (latencyMs * 0.3)
                ));
            }
        } else {
            metrics.setFailureCount(metrics.getFailureCount() + 1);
            metrics.setLastFailureAt(LocalDateTime.now());
        }

        // Calculate error rate
        if (metrics.getTotalRequests() > 0) {
            double errorRate = (double) metrics.getFailureCount() / metrics.getTotalRequests() * 100;
            metrics.setErrorRatePercent(errorRate);
        }

        healthMetricsRepository.save(metrics);
    }

    @Override
    public List<NotificationProvider> findByChannelOrderByPriority(NotificationChannel channel) {
        log.info("finding notification providers with channel: {} ordered by priority", channel);
        return providerRepository.findByChannelOrderByPriority(channel).stream()
                .map(NotificationProviderEntity::toDto)
                .toList();
    }

    @Override
    public ProviderHealthMetrics saveHealthMetrics(ProviderHealthMetrics metrics) {
        log.info("saving provider health metrics: {}", metrics);
        return healthMetricsRepository.save(ProviderHealthMetricsEntity.toEntity(metrics)).toDto();
    }

    @Override
    public Optional<ProviderHealthMetrics> findLatestHealthMetrics(String providerId) {
        log.info("finding latest health metrics for providerId: {}", providerId);
        return healthMetricsRepository.findTopByProviderIdOrderByTimestampDesc(providerId)
                .map(ProviderHealthMetricsEntity::toDto);
    }

    @Override
    public List<ProviderHealthMetrics> findHealthMetrics(String providerId, LocalDateTime from, LocalDateTime to) {
        log.info("finding health metrics for providerId: {} from: {} to: {}", providerId, from, to);
        return healthMetricsRepository.findByProviderIdAndTimestampBetween(providerId, from, to).stream()
                .map(ProviderHealthMetricsEntity::toDto)
                .toList();
    }
}
