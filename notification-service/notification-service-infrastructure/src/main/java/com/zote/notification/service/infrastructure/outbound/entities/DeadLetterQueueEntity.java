package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.notification.service.domain.model.DeadLetterQueueItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "dead_letter_queue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterQueueEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "notification_id", nullable = false)
    private String notificationId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "error_type")
    private String errorType;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_stack_trace", columnDefinition = "TEXT")
    private String errorStackTrace;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "scheduled_retry_at")
    private LocalDateTime scheduledRetryAt;

    private Boolean processed;

    public static DeadLetterQueueEntity toEntity(DeadLetterQueueItem deadLetterQueueItem) {
        DeadLetterQueueEntity entity = new DeadLetterQueueEntity();
        BeanUtils.copyProperties(deadLetterQueueItem, entity);
        return entity;
    }

    public DeadLetterQueueItem toDto() {
        DeadLetterQueueItem deadLetterQueueItem = new DeadLetterQueueItem();
        BeanUtils.copyProperties(this, deadLetterQueueItem);
        return deadLetterQueueItem;
    }

}
