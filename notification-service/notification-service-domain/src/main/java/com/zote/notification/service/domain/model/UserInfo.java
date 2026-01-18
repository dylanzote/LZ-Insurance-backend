package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User information model
 * Represents user data fetched from user-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private Status status;
    private boolean emailConfirmed;
    private boolean newUser;
    private Gender gender;
    private String town;
    private String address;
    private String locale;
    private String imageUrl;
    private String avatar; // Alias for imageUrl for frontend compatibility
    private String branchId;
    private String department;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    // User roles for notification preference determination
    private Set<UserRole> roles;
    
    /**
     * Simple role information for notification preferences
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRole {
        private String id;
        private String name;
        
        /**
         * Determines notification preferences:
         * - true: Customer role (push notifications + bilingual emails)
         * - false: System role (web notifications + critical emails)
         */
        private boolean isCustomerRole;
    }

}

