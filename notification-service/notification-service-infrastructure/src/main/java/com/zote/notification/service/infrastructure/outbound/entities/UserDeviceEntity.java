package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.DevicePlatform;
import com.zote.notification.service.domain.model.UserDevice;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDeviceEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DevicePlatform platform;

    @Column(name = "platform_version")
    private String platformVersion;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "push_token", unique = true)
    private String pushToken;  // Specific Firebase token (Android/Web)

    @Column(name = "fcm_token")
    private String fcmToken;  // Specific Firebase token (Android/Web)

    @Column(name = "apns_token")
    private String apnsToken;  // Specific Apple token (iOS/macOS)

    @Column(name = "is_expo")
    private Boolean isExpo = false;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "device_language")
    private String deviceLanguage;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subscribed_topics", columnDefinition = "jsonb")
    private Set<String> subscribedTopics;

    public static UserDeviceEntity toEntity(UserDevice userDevice) {
        UserDeviceEntity entity = new UserDeviceEntity();
        BeanUtils.copyProperties(userDevice, entity);

        // Set isExpo based on platform
        if (userDevice.getPlatform() != null) {
            entity.setIsExpo(isExpoPlatform(userDevice.getPlatform()));
        }

        return entity;
    }

    public UserDevice toDto() {
        UserDevice userDevice = new UserDevice();
        BeanUtils.copyProperties(this, userDevice);
        return userDevice;
    }

    private static boolean isExpoPlatform(DevicePlatform platform) {
        return platform == DevicePlatform.EXPO_IOS ||
                platform == DevicePlatform.EXPO_ANDROID ||
                platform == DevicePlatform.EXPO_WEB;
    }
}
