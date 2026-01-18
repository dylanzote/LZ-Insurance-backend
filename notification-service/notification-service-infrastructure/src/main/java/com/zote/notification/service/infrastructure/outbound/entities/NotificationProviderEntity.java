package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.NotificationProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Entity
@Table(name = "notification_providers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationProviderEntity extends Auditable {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> config;

    private Integer priority;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "max_rate_per_minute")
    private Integer maxRatePerMinute;

    @Column(name = "circuit_breaker_enabled")
    private Boolean circuitBreakerEnabled;

    @Column(name = "circuit_breaker_threshold")
    private Integer circuitBreakerThreshold;

    public static NotificationProviderEntity toEntity(NotificationProvider notificationProvider) {
        NotificationProviderEntity entity = new NotificationProviderEntity();
        BeanUtils.copyProperties(notificationProvider, entity);
        return entity;
    }

    public NotificationProvider toDto() {
        NotificationProvider notificationProvider = new NotificationProvider();
        BeanUtils.copyProperties(this, notificationProvider);
        return notificationProvider;
    }
}
