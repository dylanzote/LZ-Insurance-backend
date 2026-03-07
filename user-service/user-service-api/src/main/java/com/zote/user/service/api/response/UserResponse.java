package com.zote.user.service.api.response;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.user.service.domain.model.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserResponse {
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
    private boolean twoFactorEnabled; // Whether two-factor authentication is enabled
    private TwoFacMethod twoFactorMethod; // Two-factor authentication method (EMAIL or SMS)
    private AuthResponse authResponse;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin; // Last login timestamp

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        userResponse.setRoles(user.getRoles().stream().map(RoleResponse::toResponse).collect(Collectors.toSet()));
        // Set aliases for frontend compatibility
        userResponse.setAvatar(user.getImageUrl());
        userResponse.setPhone(user.getPhoneNumber());
        return userResponse;
    }
}
