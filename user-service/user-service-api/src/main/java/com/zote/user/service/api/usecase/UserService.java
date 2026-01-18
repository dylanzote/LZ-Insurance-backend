package com.zote.user.service.api.usecase;

import com.zote.common.utils.enums.SortField;
import com.zote.user.service.api.controller.UserApi;
import com.zote.user.service.api.request.BulkUserOperationRequest;
import com.zote.user.service.api.request.CreateAdminUserRequest;
import com.zote.user.service.api.request.CreateUserRequest;
import com.zote.user.service.api.request.UpdatePasswordRequest;
import com.zote.user.service.api.request.UpdateUserRequest;
import com.zote.user.service.api.response.BulkOperationResult;
import com.zote.user.service.api.response.ImageDto;
import com.zote.user.service.api.response.UserActivityResponse;
import com.zote.user.service.api.response.UserPageResponse;
import com.zote.user.service.api.response.UserResponse;
import com.zote.user.service.api.response.UserSessionResponse;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.user.service.domain.ports.inbound.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserApi {

    private final UserPort userPort;
    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("incoming request for creating User {}", createUserRequest);
        return UserResponse.toResponse(userPort.createUser(createUserRequest.tocreateUserData()));
    }

    @Override
    public UserResponse createUserByAdmin(CreateAdminUserRequest createAdminUserRequest) {
        log.info("incoming request for creating User by admin {}", createAdminUserRequest);
        return UserResponse.toResponse(userPort.createUserByAdmin(createAdminUserRequest.toCreateAdminUserData()));
    }

    @Override
    public void resendPasswordResetEmail(String userId) {
        log.info("incoming request to resend password reset email for user: {}", userId);
        userPort.resendPasswordResetEmail(userId);
    }

    @Override
    public UserPageResponse getAllUser(Integer pageNo, Integer sizePerPage, SortField sortField, Sort.Direction sortDirection) {
        log.info("incoming get all users by page request with page {}, sizePerPage {}, SortField {}, SortOrder {}", pageNo, sizePerPage, sortField, sortDirection);
        return new UserPageResponse(userPort.getAllUsersByPage(pageNo, sizePerPage, sortField.getFieldName(), sortDirection).map(UserResponse::toResponse));
    }

    @Override
    public UserPageResponse getAllUserAgent(Integer pageNo, Integer sizePerPage, SortField sortField, Sort.Direction sortDirection, String roleName) {
        log.info("incoming get all users  agents by page request with page {}, sizePerPage {}, SortField {}, SortOrder {}", pageNo, sizePerPage, sortField, sortDirection);
        return new UserPageResponse(userPort.getAllUsersByRoleNamePage(pageNo, sizePerPage, sortField.getFieldName(), sortDirection,roleName).map(UserResponse::toResponse));
    }

    @Override
    public UserResponse getUser(String id) {
        log.info("incoming request for getting user with id {}", id);
        return UserResponse.toResponse(userPort.findUserById(id));
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        log.info("incoming request for updating user {}", updateUserRequest);
        return UserResponse.toResponse(userPort.updateUser(updateUserRequest.toUserUpdateData()));
    }

    @Override
    public void deleteUser(String id) {
        log.info("incoming request for deleting user with id {}", id);
        userPort.deleteUser(id);
    }

    @Override
    public void validateUserEmail(String email) {
        log.info("incoming request for validating user email {}", email);
        userPort.validateUserEmail(email);
    }

    @Override
    public void updateUserPassword(UpdatePasswordRequest updatePasswordRequest) {
        log.info("incoming request for updating user password {}", updatePasswordRequest);
        userPort.updateUserPassword(updatePasswordRequest.toPasswordUpdateData());
    }

    @Override
    public ImageDto uploadUserImage(String userId, MultipartFile image) {
        log.info("incoming request for uploading user image for user with id {}", userId);
        return ImageDto.builder().imageUrl(userPort.uploadUserImage(userId, image)).build();
    }

    @Override
    public ImageDto getUserImage(String id) {
        log.info("incoming request for getting user image link for user");
        return ImageDto.builder().imageUrl(userPort.getUserImageUrl(id)).build();
    }

    @Override
    public String getUserImageBase64(String userId) {
        log.info("incoming request for getting user image base64 for user");
        return userPort.getUserImage(userId);
    }

    @Override
    public UserResponse activateUser(String id) {
        log.info("incoming request for activating user with id {}", id);
        return UserResponse.toResponse(userPort.activateUser(id));
    }

    @Override
    public UserResponse suspendUser(String id) {
        log.info("incoming request for suspending user with id {}", id);
        return UserResponse.toResponse(userPort.suspendUser(id));
    }

    @Override
    public UserPageResponse getAllUsersFiltered(Integer pageNo, Integer sizePerPage, SortField sortField, Sort.Direction sortDirection, String role, String status, String department) {
        log.info("incoming get all users filtered request with page {}, sizePerPage {}, role {}, status {}, department {}", pageNo, sizePerPage, role, status, department);
        return new UserPageResponse(userPort.getAllUsersFiltered(pageNo, sizePerPage, sortField.getFieldName(), sortDirection, role, status, department).map(UserResponse::toResponse));
    }

    @Override
    public List<UserActivityResponse> getActivities(String userId) {
        log.info("incoming request for getting user activities for userId: {}", userId);
        return userPort.getUserActivities(userId).stream()
                .map(UserActivityResponse::toResponse)
                .toList();
    }

    @Override
    public List<String> getDepartments() {
        log.info("incoming request for getting all departments");
        return userPort.getAllDepartments();
    }

    @Override
    public void impersonateUser(String userId) {
        log.info("incoming request for impersonating user with id: {}", userId);
        userPort.impersonateUser(userId);
    }

    @Override
    public void stopImpersonation() {
        log.info("incoming request to stop impersonation");
        userPort.stopImpersonation();
    }

    @Override
    public UserResponse getProfile() {
        log.info("incoming request for getting profile");
        return UserResponse.toResponse(userPort.getProfile());
    }

    @Override
    public void sendVerificationCode(String method) {
        log.info("incoming request for sending verification code via {}", method);
        userPort.sendTwoFactorCode(TwoFacMethod.valueOf(method.toUpperCase()));
    }

    @Override
    public void verifyTwoStepCode(String code, String method) {
        log.info("incoming request for verifying two-step code");
        userPort.verifyTwoFactorCode(code, TwoFacMethod.valueOf(method.toUpperCase()));
    }

    @Override
    public void disableTwoStepVerification() {
        log.info("incoming request for disabling two-step verification");
        userPort.disableTwoFactor();
    }

    @Override
    public List<String> generateBackupCodes() {
        log.info("incoming request for generating backup codes");
        return userPort.generateBackupCodes();
    }

    @Override
    public boolean verifyBackupCode(String code) {
        log.info("incoming request for verifying backup code");
        return userPort.verifyBackupCode(code);
    }

    @Override
    public int getRemainingBackupCodesCount() {
        log.info("incoming request for getting remaining backup codes count");
        return userPort.getRemainingBackupCodesCount();
    }

    @Override
    public void adminUnlockAccount(String userId) {
        log.info("incoming request for admin force unlock of user: {}", userId);
        userPort.adminUnlockAccount(userId);
    }

    @Override
    public String exportActivitiesCsv(String userId, String startDate, String endDate) {
        log.info("incoming request for exporting activities to CSV (user: {}, start: {}, end: {})", 
                userId, startDate, endDate);
        return userPort.exportActivitiesCsv(userId, startDate, endDate);
    }

    @Override
    public org.springframework.data.domain.Page<UserActivityResponse> searchActivities(
            String query, String userId, String action, int page, int size) {
        log.info("incoming request for searching activities (query: {}, userId: {}, action: {}, page: {}, size: {})",
                query, userId, action, page, size);
        return userPort.searchActivities(query, userId, action, page, size)
                .map(UserActivityResponse::toResponse);
    }

    @Override
    public BulkOperationResult bulkUserOperation(BulkUserOperationRequest request) {
        log.info("incoming request for bulk operation: {} on {} users", 
                request.getOperation(), request.getUserIds().size());
        var result = userPort.bulkUserOperation(
                request.getUserIds(), 
                request.getOperation(), 
                request.getRoleId()
        );
        return BulkOperationResult.builder()
                .totalRequested(result.totalRequested())
                .successCount(result.successCount())
                .failureCount(result.failureCount())
                .successfulUserIds(result.successfulUserIds())
                .failures(result.failures())
                .build();
    }

    @Override
    public List<UserSessionResponse> getMySessions() {
        log.info("incoming request for getting user sessions");
        return userPort.getUserSessions().stream()
                .map(session -> UserSessionResponse.builder()
                        .sessionId(session.sessionId())
                        .userId(session.userId())
                        .deviceType(session.deviceType())
                        .ipAddress(session.ipAddress())
                        .userAgent(session.userAgent())
                        .createdAt(session.createdAt())
                        .lastAccessedAt(session.lastAccessedAt())
                        .isCurrentSession(session.isCurrentSession())
                        .build())
                .toList();
    }

    @Override
    public void revokeSession(String sessionId) {
        log.info("incoming request for revoking session: {}", sessionId);
        userPort.revokeSession(sessionId);
    }

    @Override
    public void revokeAllOtherSessions() {
        log.info("incoming request for revoking all other sessions");
        userPort.revokeAllOtherSessions();
    }
}
