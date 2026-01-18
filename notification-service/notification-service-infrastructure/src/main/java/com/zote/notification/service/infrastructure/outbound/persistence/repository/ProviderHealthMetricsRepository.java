package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.notification.service.infrastructure.outbound.entities.ProviderHealthMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderHealthMetricsRepository extends JpaRepository<ProviderHealthMetricsEntity, String> {

    Optional<ProviderHealthMetricsEntity> findByProviderId(String providerId);

    List<ProviderHealthMetricsEntity> findByProviderIdAndTimestampBetween(String providerId, LocalDateTime start, LocalDateTime end);

    Optional<ProviderHealthMetricsEntity> findTopByProviderIdOrderByTimestampDesc(String providerId);
}
