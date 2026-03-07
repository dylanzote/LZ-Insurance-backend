package com.zote.user.service.domain.usecase;

import com.zote.common.utils.config.BeanConfig;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.enums.TokenType;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.files.MinioObjectStorage;
import com.zote.keycloak.adapter.KeyCloakService;
import com.zote.user.service.domain.model.*;
import com.zote.user.service.domain.model.AuthData;
import com.zote.user.service.domain.ports.inbound.UserPort;
import com.zote.user.service.domain.ports.outbound.RoleRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserActivityRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import com.zote.user.service.domain.support.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserImpl implements UserPort {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserSupport userSupport;
    private final BeanConfig config;
    private final MinioObjectStorage minioObjectStorage;
    private final KeyCloakService keyCloakService;
    private final UserActivityRepositoryPort userActivityRepositoryPort;
    private final ActivityLogger activityLogger;
    private final TokenService tokenService;
    private final MessagingSupport messagingSupport;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    private final ImpersonationService impersonationService;
    private final BackupCodeService backupCodeService;
    private final ActivityExportService activityExportService;


    @Override
    public User createUser(CreateUserData createUserData) {
        userSupport.validateData(createUserData);
        verifyIfUserExists(createUserData);
        var roles = getRoles(createUserData.getRoleIds());
        var user = userSupport.buildUser(createUserData, roles);
        var keycloakUserId = keyCloakService.createUser(user.toKeyCloakUser(), createUserData.getPassword());
        user.setKeycloakUserId(keycloakUserId);
        roles.forEach(role -> keyCloakService.assignRoleToUser(keycloakUserId, role.getName()));
        user = userRepository.saveUser(user);
        var authRequest = userSupport.buildAuthRequest(createUserData.getEmail(), createUserData.getPassword());
        var authData = userSupport.authenticateKeycloakUser(authRequest);
        user.setAuthResponse(authData);
        activityLogger.logActivity(user.getId(), "create", "user", user.getId());

        messagingSupport.publishUserCreatedEvent(user);
        return user;
    }


    @Override
    public User createUserByAdmin(CreateAdminUserData createAdminUserData) {
        userSupport.validateData(createAdminUserData);
        var createUserData = createAdminUserData.tocreateUserData();
        verifyIfUserExists(createUserData);
        var password = userSupport.generateRandomPassword();
        createUserData.setPassword(password);
        var roles = getRoles(createAdminUserData.getRoleIds());
        var user = userSupport.buildUser(createUserData, roles);
        var keycloakUserId = keyCloakService.createUser(user.toKeyCloakUser(), password);
        user.setKeycloakUserId(keycloakUserId);
        roles.forEach(role -> keyCloakService.assignRoleToUser(keycloakUserId, role.getName()));

        user.setMustChangePassword(true);
        log.info("Setting mustChangePassword=true for admin-created user: {}", createUserData.getEmail());
        user = userRepository.saveUser(user);

        var currentAdmin = userSupport.getCurrentUser();
        activityLogger.logActivity(currentAdmin.getId(), "create", "user", user.getId());

        var passwordResetToken = tokenService.generatePasswordResetToken(user);
        log.info("Generated password reset token for admin-created user: {}", user.getId());

        messagingSupport.publishAdminCreatedUserEvent(user, passwordResetToken.getToken(), passwordResetToken.getExpiresAt().getHour(), currentAdmin.getId());
        return user;
    }

    @Override
    public void resendPasswordResetEmail(String userId) {
        log.info("Resending password reset email for user: {}", userId);
        var user = userRepository.findUserById(userId);

        if (!user.isMustChangePassword()) {
            log.warn("User {} does not require password change, resend not applicable", userId);
            throw new FunctionalError("This user does not require a password reset");
        }
        var passwordResetToken = tokenService.generatePasswordResetToken(user);
        log.info("Generated new password reset token for user: {}", userId);
        var currentAdmin = userSupport.getCurrentUser();

        messagingSupport.publishAdminCreatedUserEvent(user, passwordResetToken.getToken(), passwordResetToken.getExpiresAt().getHour(), currentAdmin.getId());
        activityLogger.logActivity(currentAdmin.getId(), "resend_password_reset", "user", userId);
        log.info("Resent password reset email to user: {} by admin: {}", userId, currentAdmin.getId());
    }

    @Override
    public User updateUser(UserData userData) {
        var user  = userRepository.findUserById(userData.getId());

        twoFactorAuthenticationService.verifyTwoFactorEnabled(user, userData.getTwoFactorCode());
        user.getRoles().forEach(role -> keyCloakService.removeRoleToUser(user.getKeycloakUserId(), role.getName()));
        userSupport.validateData(userData);
        var roles = getRoles(userData.getRoleIds());
        var updatedUser = userData.toUser(user);
        updatedUser.setRoles(roles);
        keyCloakService.updateUser(updatedUser.toKeyCloakUpdateUser());
        roles.forEach(role -> keyCloakService.assignRoleToUser(updatedUser.getKeycloakUserId(), role.getName()));
        User savedUser = userRepository.saveUser(updatedUser);
        
        var currentUser = userSupport.getCurrentUser();
        activityLogger.logActivity(currentUser.getId(), "update", "user", savedUser.getId());
        
        // TODO: Optimize updated fields tracking
        String updatedFields = "profile,roles";
        messagingSupport.publishUserUpdatedEvent(savedUser, updatedFields);
        
        return savedUser;
    }


    @Override
    public boolean verifyPassword(String userId, String password) {
        var user = userRepository.findUserById(userId);
        return config.passwordEncoder().matches(password, user.getPassword());
    }

    @Override
    public void updateUserPassword(PasswordUpdateData passwordUpdateData) {
        var user = userRepository.findUserById(passwordUpdateData.getUserId());

        twoFactorAuthenticationService.verifyTwoFactorEnabled(user, passwordUpdateData.getTwoFactorCode());

        userSupport.validatePasswords(passwordUpdateData.getNewPassword());
        
        if (config.passwordEncoder().matches(passwordUpdateData.getOldPassword(), user.getPassword()) && !passwordUpdateData.getOldPassword().equals(passwordUpdateData.getNewPassword())) {
            keyCloakService.resetPassword(user.getKeycloakUserId(), passwordUpdateData.getNewPassword());
            user.setPassword(config.passwordEncoder().encode(passwordUpdateData.getNewPassword()));

            if (user.isMustChangePassword()) {
                user.setMustChangePassword(false);
                log.info("Cleared mustChangePassword flag for user: {}", user.getId());
            }
            user.setPasswordChangedAt(LocalDateTime.now());
            log.info("Updated passwordChangedAt for user: {}", user.getId());
            
            userRepository.saveUser(user);
            activityLogger.logActivity(user.getId(), "update", "password", user.getId());
            messagingSupport.publishPasswordChangedEvent(user, "USER", null, null);
        } else
            throw new FunctionalError("old password does not match existing password or old password cannot be same as new password");
    }

    @Override
    public void deleteUser(String userId) {
        var user = userRepository.findUserById(userId);
        keyCloakService.deleteUser(user.getKeycloakUserId());
        userRepository.deleteUserById(userId);
        activityLogger.logActivity(userId, "delete", "user", userId);
    }

    @Override
    public void validateUserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new FunctionalError("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new FunctionalError("Invalid email format");
        }
        if (userRepository.existsByEmail(email)) {
            throw new FunctionalError("Email is already in use");
        }
    }

    @Override
    public User findUserById(String userId) {
        var user = userRepository.findUserById(userId);
        user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        var user = userRepository.findUserByEmail(email);
        user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
        return user;
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) {
        var user = userRepository.findUserByPhoneNumber(phoneNumber);
        user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(user -> {
                    user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
                    return user;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<User> getAllUsersByPage(int page, int sizePerPage, String sortField, Sort.Direction sortDirection) {
        var pageNo = page < 0 ? 0 : page - 1;
        var pageable = PageRequest.of(pageNo, sizePerPage, sortDirection, sortField);
        return userRepository.findUsersByPage(pageable).map(user -> {
            user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
            return user;
        });
    }

    @Override
    public String uploadUserImage(String userId, MultipartFile image) {
        if (!userRepository.existsById(userId)) {
            throw new FunctionalError("User does not exist with given id");
        }
        minioObjectStorage.uploadImage(image, minioObjectStorage.getUserImageName(userId));
        activityLogger.logActivity(userId, "upload", "profile_image", userId);
        return minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(userId));
    }

    @Override
    public String getUserImage(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new FunctionalError("User does not exist with given id");
        }
        return MinioObjectStorage.convertToBase64(minioObjectStorage.getObject(minioObjectStorage.getUserImageName(userId)));
    }

    @Override
    public String getUserImageUrl(String userId) {
        return minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(userId));
    }

    @Override
    public User findUserByUserName(String username) {
        var user = userRepository.findUserByUserName(username);
        user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
        return user;
    }

    @Override
    public Page<User> getAllUsersByRoleNamePage(int page, int sizePerPage, String sortField, Sort.Direction sortDirection, String roleName) {
        var pageNo = page < 0 ? 0 : page - 1;
        var pageable = PageRequest.of(pageNo, sizePerPage, sortDirection, sortField);
        return userRepository.findUsersAgentByPage(roleName, pageable).map(user -> {
            user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
            return user;
        });
    }

    @Override
    public User activateUser(String userId) {
        var user = userRepository.findUserById(userId);
        user.setStatus(Status.ACTIVE);
        User activatedUser = userRepository.saveUser(user);
        var currentUser = userSupport.getCurrentUser();
        activityLogger.logActivity(currentUser.getId(), "activate", "user", userId);
        messagingSupport.publishUserActivatedEvent(activatedUser, currentUser.getId());
        return activatedUser;
    }

    @Override
    public User suspendUser(String userId) {
        var user = userRepository.findUserById(userId);
        user.setStatus(Status.SUSPENDED);
        User suspendedUser = userRepository.saveUser(user);
        var currentUser = userSupport.getCurrentUser();
        activityLogger.logActivity(currentUser.getId(), "suspend", "user", userId);
        messagingSupport.publishUserSuspendedEvent(suspendedUser, currentUser.getId(), "Account suspended by administrator");
        return suspendedUser;
    }

    @Override
    public Page<User> getAllUsersFiltered(int page, int sizePerPage, String sortField, Sort.Direction sortDirection, String role, String status, String department) {
        var pageNo = page < 0 ? 0 : page - 1;
        var pageable = PageRequest.of(pageNo, sizePerPage, sortDirection, sortField);
        Page<User> users = userRepository.findUsersFiltered(role, status, department, pageable);
        return users.map(user -> {
            user.setImageUrl(minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())));
            return user;
        });
    }

    @Override
    public List<UserActivity> getUserActivities(String userId) {
        log.info("Getting user activities for userId: {}", userId);
        if (userId != null && !userId.isEmpty()) {
            return userActivityRepositoryPort.findActivitiesByUserId(userId);
        } else {
            return userActivityRepositoryPort.findAllActivities();
        }
    }

    @Override
    public List<String> getAllDepartments() {
        log.info("Getting all departments");
        List<User> allUsers = userRepository.getAllUsers();
        return allUsers.stream()
                .map(User::getDepartment)
                .filter(dept -> dept != null && !dept.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    @Override
    public AuthData impersonateUser(String userId) {
        log.info("Impersonating user: {} ", userId);
        return impersonationService.impersonateUser(userId);
    }

    @Override
    public void stopImpersonation() {
        log.info("Stopping impersonation");
        var adminUser = userSupport.getCurrentUser();
        tokenService.markAllUserTokensAsUsed(adminUser.getId(), TokenType.IMPERSONATION);
        activityLogger.logActivity(adminUser.getId(), "stop_impersonation", "user", null);
    }

    private void verifyIfUserExists(CreateUserData createUserData) {
        if (userRepository.existsByEmail(createUserData.getEmail())) {
            throw new FunctionalError("User already exist with email");
        }
        if (userRepository.existsByPhoneNumber(createUserData.getPhoneNumber())) {
            throw new FunctionalError("User already exist with phoneNumber");
        }
    }

    private Set<Role> getRoles(Set<String> roleIds) {
        return roleIds.stream()
               .map(roleRepository::findRoleById)
               .collect(Collectors.toSet());
    }

    @Override
    public User getProfile() {
        return userSupport.getCurrentUser();
    }

    @Override
    public User updateProfile(UserData userData) {
        var currentUser = userSupport.getCurrentUser();
        userData.setId(currentUser.getId());
        return updateUser(userData);
    }


    @Override
    public void sendTwoFactorCode(TwoFacMethod method) {
        log.info("Send verification code request for method: {}", method);
        var currentUser = userSupport.getCurrentUser();
        sendTwoFactorCode(currentUser, method);
    }

    @Override
    public void verifyTwoFactorCode(String code, TwoFacMethod method) {
        log.info("Verify two-factor code and enable 2FA for method: {}", method);
        var currentUser = userSupport.getCurrentUser();
        twoFactorAuthenticationService.verifyAndEnableTwoFactor(currentUser, code, method);
    }

    @Override
    public void verify2FACode(String code) {
        log.info("Verify 2FA code for sensitive operation");
        var currentUser = userSupport.getCurrentUser();

        if (!currentUser.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_NOT_ENABLED);
        }

        twoFactorAuthenticationService.validateVerificationCodeFormat(code);

        boolean isValid = twoFactorAuthenticationService.verifyTwoFactorCode(currentUser.getId(), code);
        if (!isValid) {
            throw new FunctionalError(SecurityConstants.ERROR_INVALID_2FA_CODE);
        }
        
        log.info("2FA code verified successfully for user: {}", currentUser.getId());
    }

    @Override
    public void disableTwoFactor() {
        log.info("Disable two-step verification request");
        var currentUser = userSupport.getCurrentUser();
        disableTwoFactor(currentUser);
    }


    @Override
    public List<String> generateBackupCodes() {
        log.info("Generating backup codes for current user");
        var user = userSupport.getCurrentUser();
        return backupCodeService.generateBackupCodes(user.getId());
    }

    @Override
    public boolean verifyBackupCode(String code) {
        log.info("Verifying backup code for current user");
        var user = userSupport.getCurrentUser();
        return backupCodeService.verifyBackupCodeForCurrentUser(user, code);
    }

    @Override
    public int getRemainingBackupCodesCount() {
        log.info("Getting remaining backup codes count for current user");
        var user = userSupport.getCurrentUser();
        return backupCodeService.getRemainingBackupCodesCountForCurrentUser(user);
    }


    @Override
    public void adminUnlockAccount(String userId) {
        log.info("Admin force unlocking account for user: {}", userId);
        
        var user = userRepository.findUserById(userId);
        if (user.getAccountLockedUntil() == null || user.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
            log.warn("Attempted to unlock account that is not locked: {}", userId);
            throw new FunctionalError("Account is not locked");
        }
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.saveUser(user);
        var adminUser = userSupport.getCurrentUser();

        activityLogger.logActivity(userId, "account_unlocked", "admin", "Unlocked by admin: " + adminUser.getId());
        activityLogger.logActivity(adminUser.getId(), "unlock_user_account", "admin", "Unlocked account for user: " + userId);
        log.info("Account unlocked successfully by admin {} for user: {}", adminUser.getId(), userId);
    }

    @Override
    public String exportActivitiesCsv(String userId, String startDate, String endDate) {
       log.info("Exporting activities to CSV");
        var currentUser = userSupport.getCurrentUser();
        if (userId == null || userId.isEmpty()) {
            validateAdminPermission(currentUser);
        }
        String csvContent = activityExportService.exportActivitiesToCsv(userId, startDate, endDate);
        activityLogger.logActivity(currentUser.getId(), "export_activities", "user", createExportDetails(userId, startDate, endDate, csvContent));
        return csvContent;
    }

    @Override
    public Page<UserActivity> searchActivities(String query, String userId, String action, int page, int size) {
        log.info("Searching activities");
        
        List<UserActivity> activities;
        if (userId != null) {
            activities = userActivityRepositoryPort.findActivitiesByUserId(userId);
        } else {
            activities = userActivityRepositoryPort.findAllActivities();
        }
        
        // Simple filtering
        if (action != null) {
            activities = activities.stream()
                    .filter(a -> a.getAction().equalsIgnoreCase(action))
                    .toList();
        }
        
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            activities = activities.stream()
                    .filter(a -> 
                        a.getAction().toLowerCase().contains(lowerQuery) ||
                        (a.getResourceId() != null && a.getResourceId().toLowerCase().contains(lowerQuery)) ||
                        a.getResource().toLowerCase().contains(lowerQuery)
                    )
                    .toList();
        }

        int start = page * size;
        int end = Math.min(start + size, activities.size());
        List<UserActivity> paged = activities.subList(Math.min(start, activities.size()), end);
        
        return new PageImpl<>(paged, PageRequest.of(page, size), activities.size());
    }


    @Override
    public BulkOperationResult bulkUserOperation(List<String> userIds, String operation, String roleId) {
        log.info("Processing bulk operation: {} for {} users", operation, userIds.size());
        
        List<String> successfulUserIds = new ArrayList<>();
        Map<String, String> failures = new HashMap<>();
        
        for (String userId : userIds) {
            try {
                switch (operation.toUpperCase()) {
                    case "ACTIVATE":
                        activateUser(userId);
                        break;
                    case "SUSPEND":
                        suspendUser(userId);
                        break;
                    case "DELETE":
                        deleteUser(userId);
                        break;
                    case "ASSIGN_ROLE":
                        if (roleId == null) {
                            throw new FunctionalError("Role ID is required for ASSIGN_ROLE operation");
                        }
                        assignRoleToUser(userId, roleId);
                        break;
                    case "REMOVE_ROLE":
                        if (roleId == null) {
                            throw new FunctionalError("Role ID is required for REMOVE_ROLE operation");
                        }
                        removeRoleFromUser(userId, roleId);
                        break;
                    default:
                        throw new FunctionalError("Invalid operation: " + operation);
                }
                successfulUserIds.add(userId);
                log.info("Successfully performed {} operation on user: {}", operation, userId);
                
            } catch (Exception e) {
                log.error("Failed to perform {} operation on user: {}", operation, userId, e);
                failures.put(userId, e.getMessage());
            }
        }
        
        // Log bulk operation
        User adminUser = userSupport.getCurrentUser();
        activityLogger.logActivity(adminUser.getId(), "bulk_operation", "user", 
                String.format("Operation: %s, Total: %d, Success: %d, Failed: %d", 
                        operation, userIds.size(), successfulUserIds.size(), failures.size()));
        
        return new BulkOperationResult(
                userIds.size(),
                successfulUserIds.size(),
                failures.size(),
                successfulUserIds,
                failures
        );
    }
    
    private void assignRoleToUser(String userId, String roleId) {
        User user = userRepository.findUserById(userId);
        Role role = roleRepository.findRoleById(roleId);
        
        if (user.getRoles().stream().anyMatch(r -> r.getId().equals(roleId))) {
            throw new FunctionalError("User already has this role");
        }
        
        user.getRoles().add(role);
        userRepository.saveUser(user);
        keyCloakService.assignRoleToUser(user.getKeycloakUserId(), role.getName());
        
        activityLogger.logActivity(userId, "role_assigned", "user", "Role: " + role.getName());
        log.info("Assigned role {} to user {}", roleId, userId);
    }
    
    private void removeRoleFromUser(String userId, String roleId) {
        User user = userRepository.findUserById(userId);
        Role role = roleRepository.findRoleById(roleId);
        
        if (user.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
            throw new FunctionalError("User does not have this role");
        }
        
        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.saveUser(user);
        keyCloakService.removeRoleToUser(user.getKeycloakUserId(), role.getName());
        
        activityLogger.logActivity(userId, "role_removed", "user", "Role: " + role.getName());
        log.info("Removed role {} from user {}", roleId, userId);
    }

    @Override
    public List<UserSession> getUserSessions() {
        log.info("Retrieving user sessions");
        User currentUser = userSupport.getCurrentUser();

        
        return List.of(
            new UserSession(
                "session-1",
                currentUser.getId(),
                "WEB",
                "192.168.1.1",
                "Mozilla/5.0...",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now(),
                true
            )
        );
    }

    @Override
    public void revokeSession(String sessionId) {
        log.info("Revoking session: {}", sessionId);
        User currentUser = userSupport.getCurrentUser();
        
        // Call Keycloak to revoke the session
        // keyCloakService.revokeSession(currentUser.getKeycloakId(), sessionId);
        
        activityLogger.logActivity(currentUser.getId(), "session_revoked", SecurityConstants.MODULE_AUTH, 
                "Session ID: " + sessionId);
        log.warn("Session revocation not fully integrated with Keycloak yet.");
    }

    @Override
    public void revokeAllOtherSessions() {
        log.info("Revoking all other sessions");
        User currentUser = userSupport.getCurrentUser();
        
        // Call Keycloak to revoke all sessions except current
        // keyCloakService.revokeAllOtherSessions(currentUser.getKeycloakId(), currentSessionId);
        
        activityLogger.logActivity(currentUser.getId(), "all_sessions_revoked", SecurityConstants.MODULE_AUTH, null);
        log.warn("Session revocation not fully integrated with Keycloak yet.");
    }

    public void disableTwoFactor(User user) {
        twoFactorAuthenticationService.disableTwoFactor(user);
    }


    public String sendTwoFactorCode(User user, TwoFacMethod method) {
        return twoFactorAuthenticationService.sendTwoFactorCode(user, method);
    }

    @Transactional
    public List<String> generateBackupCodesForUser(String userId) {
        log.info("Admin generating backup codes for user: {}", userId);
        User adminUser = userSupport.getCurrentUser();
        validateAdminPermission(adminUser);

        List<String> backupCodes = backupCodeService.generateBackupCodes(userId);

        activityLogger.logActivity(
            adminUser.getId(),
            SecurityConstants.ACTIVITY_ADMIN_GENERATE_BACKUP_CODES,
            SecurityConstants.MODULE_USER,
            userId
        );

        return backupCodes;
    }

    @Transactional
    public void revokeBackupCodesForUser(String userId) {
        log.info("Admin revoking backup codes for user: {}", userId);
        User adminUser = userSupport.getCurrentUser();
        validateAdminPermission(adminUser);

        backupCodeService.revokeAllBackupCodes(userId);

        activityLogger.logActivity(
            adminUser.getId(),
            SecurityConstants.ACTIVITY_ADMIN_REVOKE_BACKUP_CODES,
            SecurityConstants.MODULE_USER,
            userId
        );
    }

    private void validateAdminPermission(User user) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> !role.isCustomerRole());

        if (!isAdmin) {
            throw new FunctionalError(SecurityConstants.ERROR_ADMIN_PERMISSION_REQUIRED);
        }
    }



    private String createExportDetails(String userId, String startDate, String endDate, String csvContent) {
        int recordCount = countRecordsInCsv(csvContent);
        return String.format("UserId: %s, Start: %s, End: %s, Records: %d",
                userId != null ? userId : "ALL",
                startDate != null ? startDate : "NONE",
                endDate != null ? endDate : "NONE",
                recordCount);
    }


    private int countRecordsInCsv(String csvContent) {
        if (!StringUtils.hasText(csvContent)) {
            return 0;
        }

        // Count lines minus header (if present)
        String[] lines = csvContent.split("\n");
        if (lines.length > 0 && lines[0].contains("Timestamp")) {
            return lines.length - 1;
        }
        return lines.length;
    }

}
