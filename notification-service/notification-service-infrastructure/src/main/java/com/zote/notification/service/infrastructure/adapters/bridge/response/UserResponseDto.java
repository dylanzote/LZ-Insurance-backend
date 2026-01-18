package com.zote.notification.service.infrastructure.adapters.bridge.response;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.Status;
import com.zote.notification.service.domain.model.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
public class UserResponseDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String phone; // Alias for phoneNumber for frontend compatibility
    private String dateOfBirth;
    private Set<RoleResponse> roles;
    private Status status;
    private boolean emailConfirmed;
    private boolean newUser;
    private Gender gender;
    private Language language;
    private String town;
    private String address;
    private String imageUrl;
    private String avatar; // Alias for imageUrl for frontend compatibility
    private String branchId;
    private String department;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(this, userInfo);
        
        // Map Language enum to locale string
        userInfo.setLocale(Objects.requireNonNullElse(this.language, Language.EN).getCode());
        
        // Map roles to UserInfo.UserRole
        if (this.roles != null && !this.roles.isEmpty()) {
            Set<UserInfo.UserRole> userRoles = this.roles.stream()
                    .map(roleResponse -> UserInfo.UserRole.builder()
                            .id(roleResponse.getId())
                            .name(roleResponse.getName())
                            .isCustomerRole(roleResponse.isCustomerRole())
                            .build())
                    .collect(java.util.stream.Collectors.toSet());
            userInfo.setRoles(userRoles);
        }
        
        return userInfo;
    }
}
