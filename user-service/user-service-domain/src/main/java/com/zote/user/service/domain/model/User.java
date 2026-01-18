package com.zote.user.service.domain.model;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.keycloak.adapter.model.KeyCloakUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    private String id;
    private String keycloakUserId;
    private String firstName;
    private String userName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String password;
    private Set<Role> roles;
    private Status status;
    private boolean emailConfirmed;
    private boolean newUser;
    private Gender gender;
    private Language language;
    private String town;
    private String address;
    private String imageUrl;
    private String branchId; // Reference to branch (full branch management should be separate service)
    private String department; // User's department
    private boolean twoFactorEnabled; // Whether two-factor authentication is enabled
    private TwoFacMethod twoFactorMethod; // Method: "email" or "sms"
    private String twoFactorSecret; // Secret for TOTP apps (if applicable)
    private String twoFactorBackupCodes; // JSON array of backup codes (hashed)
    private AuthData authResponse;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin; // Track last login time
    private LocalDateTime passwordChangedAt; // Track when password was last changed
    private boolean mustChangePassword; // Force password change on next login
    private int failedLoginAttempts; // Track failed login attempts for security
    private LocalDateTime accountLockedUntil; // Lock account until this time (null if not locked)

    public KeyCloakUser toKeyCloakUser() {
        KeyCloakUser keyCloakUser = new KeyCloakUser();
        BeanUtils.copyProperties(this, keyCloakUser);
        return keyCloakUser;
    }

    public  KeyCloakUser toKeyCloakUpdateUser() {
        KeyCloakUser keyCloakUser = new KeyCloakUser();
        BeanUtils.copyProperties(this, keyCloakUser);
        keyCloakUser.setId(keycloakUserId);
        return keyCloakUser;
    }
}
