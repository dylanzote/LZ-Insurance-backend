package com.zote.user.service.domain.ports.inbound;

import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.user.service.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserPort {

    User createUser(CreateUserData createUserData);

    User createUserByAdmin(CreateAdminUserData createAdminUserData);
    
    void resendPasswordResetEmail(String userId);

    User updateUser(UserData userData);

    void updateUserPassword(PasswordUpdateData passwordUpdateData);
    
    boolean verifyPassword(String userId, String password);

    void deleteUser(String userId);

    void validateUserEmail(String email);

    User findUserById(String userId);

    User findUserByEmail(String email);

    User findUserByUserName(String username);

    User findUserByPhoneNumber(String phoneNumber);

    List<User> getAllUsers();

    Page<User> getAllUsersByPage(int pageNo, int sizePerPage, String sortField, Sort.Direction sortDirection);

    Page<User> getAllUsersByRoleNamePage(int pageNo, int sizePerPage, String sortField, Sort.Direction sortDirection, String roleName);

    String uploadUserImage(String userId,  MultipartFile image);

    String getUserImage(String userId);

    String getUserImageUrl(String userId);

    User activateUser(String userId);

    User suspendUser(String userId);

    Page<User> getAllUsersFiltered(int pageNo, int sizePerPage, String sortField, Sort.Direction sortDirection, String role, String status, String department);

    List<UserActivity> getUserActivities(String userId);

    List<String> getAllDepartments();
    
    void stopImpersonation();

    User getProfile();
    
    User updateProfile(UserData userData);
    
    void sendTwoFactorCode(TwoFacMethod method);
    
    void verifyTwoFactorCode(String code, TwoFacMethod method);

    void verify2FACode(String code);

    void disableTwoFactor();

    AuthData impersonateUser(String userId);

    List<String> generateBackupCodes();
    
    boolean verifyBackupCode(String code);
    
    int getRemainingBackupCodesCount();
    
    void adminUnlockAccount(String userId);
    
    String exportActivitiesCsv(String userId, String startDate, String endDate);
    
    Page<UserActivity> searchActivities(String query, String userId, String action, int page, int size);
    
    BulkOperationResult bulkUserOperation(List<String> userIds, String operation, String roleId);
    
    List<UserSession> getUserSessions();
    
    void revokeSession(String sessionId);
    
    void revokeAllOtherSessions();
    
    record BulkOperationResult(
        int totalRequested,
        int successCount,
        int failureCount,
        List<String> successfulUserIds,
        Map<String, String> failures
    ) {}
    
    record UserSession(
        String sessionId,
        String userId,
        String deviceType,
        String ipAddress,
        String userAgent,
        LocalDateTime createdAt,
        LocalDateTime lastAccessedAt,
        boolean isCurrentSession
    ) {}
}
