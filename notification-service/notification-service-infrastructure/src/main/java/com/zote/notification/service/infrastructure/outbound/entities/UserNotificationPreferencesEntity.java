package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.UserNotificationPreferences;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "user_notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserNotificationPreferencesEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private Boolean enabled;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    private String locale;

    private String timezone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences", columnDefinition = "jsonb")
    private Map<String, Object> preferences;

    public static UserNotificationPreferencesEntity toEntity(UserNotificationPreferences userNotificationPreferences) {
        UserNotificationPreferencesEntity entity = new UserNotificationPreferencesEntity();
        BeanUtils.copyProperties(userNotificationPreferences, entity);
        return entity;
    }

    public UserNotificationPreferences toDto() {
        UserNotificationPreferences userNotificationPreferences = new UserNotificationPreferences();
        BeanUtils.copyProperties(this, userNotificationPreferences);
        return userNotificationPreferences;
    }
}
