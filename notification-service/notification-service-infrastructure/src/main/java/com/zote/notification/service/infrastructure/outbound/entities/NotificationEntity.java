package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.enums.NotificationType;
import com.zote.notification.service.domain.model.Notification;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String provider;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_variables", columnDefinition = "jsonb")
    private Map<String, Object> templateVariables;

    private String locale;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_stack_trace", columnDefinition = "TEXT")
    private String errorStackTrace;

    public static NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        BeanUtils.copyProperties(notification, entity);
        return entity;
    }

    public Notification toDto() {
        Notification notification = new Notification();
        BeanUtils.copyProperties(this, notification);
        return notification;
    }
}
