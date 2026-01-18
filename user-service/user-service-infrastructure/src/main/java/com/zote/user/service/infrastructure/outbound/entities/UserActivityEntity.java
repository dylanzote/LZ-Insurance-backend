package com.zote.user.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.user.service.domain.model.ActivityType;
import com.zote.user.service.domain.model.UserActivity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_activities")
public class UserActivityEntity extends Auditable {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private ActivityType activityType; // Legacy field, kept for backward compatibility
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    private String description; // Description of the activity
    private String ipAddress;    // IP address from where the activity was performed
    private String deviceInfo;   // Information about the device used
    private String action;       // e.g., "login", "approve", "create"
    private String resource;     // e.g., "auth", "claim", "policy"
    private String resourceId;   // ID of the resource if applicable


    public static UserActivityEntity toEntity(UserActivity userActivity, UserEntity user) {
        UserActivityEntity entity = new UserActivityEntity();
        BeanUtils.copyProperties(userActivity, entity);
        entity.setUser(user);
        return entity;
    }

    public UserActivity toDto() {
        UserActivity dto = new UserActivity();
        BeanUtils.copyProperties(this, dto);
        dto.setUserId(this.user != null ? this.user.getId() : null);
        return dto;
    }
}
