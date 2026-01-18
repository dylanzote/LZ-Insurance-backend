package com.zote.user.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.user.service.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@SQLRestriction("deleted = false")
@Table(name = "users")
public class UserEntity extends Auditable {
    @Id
    private String id;
    private String keycloakUserId;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean emailConfirmed;
    private boolean newUser;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Language language;
    private String town;
    private String address;
    private String imageUrl;
    private String branchId;
    private String department;
    private boolean twoFactorEnabled;
    private boolean twoFactorVerified;
    @Enumerated(EnumType.STRING)
    private TwoFacMethod twoFactorMethod;
    private String twoFactorSecret; // Secret for TOTP apps (if applicable)
    @Column(length = 1000)
    private String twoFactorBackupCodes; // JSON array of hashed backup codes
    private LocalDateTime lastLogin;
    @Column(nullable = false)
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private int failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime termsAcceptedAt;
    private LocalDateTime privacyPolicyAcceptedAt;
    @Column(nullable = false)
    private boolean mustChangePassword = false;

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(user, entity);
        Set<RoleEntity> roleEntities = user.getRoles()
                .stream()
                .map(RoleEntity::toEntity)
                .collect(Collectors.toSet());
        entity.setRoles(roleEntities);
        return entity;
    }

    public User toDto() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        user.setRoles(this.getRoles().stream()
                .map(RoleEntity::toDto)
                .collect(Collectors.toSet()));
        return user;
    }
    public Set<PermissionEntity> getPermissions() {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());
    }
}
