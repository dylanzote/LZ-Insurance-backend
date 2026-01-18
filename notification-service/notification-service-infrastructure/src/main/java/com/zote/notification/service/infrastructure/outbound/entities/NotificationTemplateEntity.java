package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.NotificationTemplate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Entity
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplateEntity extends Auditable {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(name = "template_path", nullable = false)
    private String templatePath;

    @Column(name = "subject_template", columnDefinition = "TEXT")
    private String subjectTemplate;

    @Column(name = "body_template", columnDefinition = "TEXT")
    private String bodyTemplate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables_schema", columnDefinition = "jsonb")
    private Map<String, Object> variablesSchema;

    @Column(name = "default_locale")
    private String defaultLocale;

    @Column(name = "is_active")
    private Boolean isActive;

    private Integer version;

    public static NotificationTemplateEntity toEntity(NotificationTemplate notificationTemplate) {
        NotificationTemplateEntity entity = new NotificationTemplateEntity();
        BeanUtils.copyProperties(notificationTemplate, entity);
        return entity;
    }

    public NotificationTemplate toDto() {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        BeanUtils.copyProperties(this, notificationTemplate);
        return notificationTemplate;
    }
}
