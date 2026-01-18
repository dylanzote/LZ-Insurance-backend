package com.zote.user.service.api.controller;

import com.zote.common.utils.enums.SortField;
import com.zote.common.utils.models.Permissions;
import com.zote.common.utils.ratelimit.RateLimit;
import com.zote.common.utils.ratelimit.RateLimitKeyStrategy;
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
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Tag(name = "User API")
@RestController
@RequestMapping("/user/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface UserApi {

    @Operation(summary = "create a new user")
    @PostMapping("create")
    UserResponse createUser(@RequestBody CreateUserRequest createUserRequest);

    @Operation(summary = "create a new user by admin")
    @PostMapping("create/by-admin")
    @RolesAllowed({Permissions.IS_ADMIN})
    UserResponse createUserByAdmin(@RequestBody CreateAdminUserRequest createAdminUserRequest);

    @Operation(summary = "resend password reset email for admin-created user")
    @PostMapping("resend-password-reset/{userId}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void resendPasswordResetEmail(@PathVariable("userId") String userId);

    @Operation(summary = "gets user by page")
    @GetMapping("get-all")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    UserPageResponse getAllUser(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "sizePerPage", defaultValue = "5") Integer sizePerPage,
                                @RequestParam(name = "sortField", defaultValue = "CREATED_ON") SortField sortField,
                                @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "gets user by roleName")
    @GetMapping("get-all/role-name")
    UserPageResponse getAllUserAgent(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "sizePerPage", defaultValue = "5") Integer sizePerPage,
                                @RequestParam(name = "sortField", defaultValue = "CREATED_ON") SortField sortField,
                                @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection,
                                @RequestParam(name = "roleName", defaultValue = "AGENT") String roleName);

    @Operation(summary = "gets user by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    UserResponse getUser(@PathVariable("id") String id);

    @Operation(summary = "updates user")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    UserResponse updateUser(@RequestBody UpdateUserRequest updateUserRequest);

    @Operation(summary = "deletes user by id")
    @DeleteMapping("delete/{id}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void deleteUser(@PathVariable("id") String id);

    @Operation(summary = "validates user with email address")
    @PostMapping("validate/{email}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    void validateUserEmail(@PathVariable("email") String email);

    @Operation(summary = "updates user password")
    @PutMapping("update/password")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    void updateUserPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest);

    @Operation(summary = "uploads user image and receives in base64 encoded format")
    @PostMapping(value = "upload/image/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ImageDto uploadUserImage(@PathVariable("userId") String userId, @RequestParam("image") MultipartFile image);

    @Operation(summary = "gets user image by id from object storage link")
    @GetMapping("get/{id}/image")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ImageDto getUserImage(@PathVariable("id") String id);

    @Operation(summary = "gets user image by id from database")
    @GetMapping("get/{userId}/base-image")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    String getUserImageBase64(@PathVariable("userId") String id);

    @Operation(summary = "activate user by id")
    @PutMapping("activate/{id}")
    @RolesAllowed({Permissions.IS_ADMIN})
    UserResponse activateUser(@PathVariable("id") String id);

    @Operation(summary = "suspend user by id")
    @PutMapping("suspend/{id}")
    @RolesAllowed({Permissions.IS_ADMIN})
    UserResponse suspendUser(@PathVariable("id") String id);

    @Operation(summary = "get all users with filters")
    @GetMapping("get-all/filtered")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    UserPageResponse getAllUsersFiltered(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "sizePerPage", defaultValue = "5") Integer sizePerPage,
            @RequestParam(name = "sortField", defaultValue = "CREATED_ON") SortField sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "department", required = false) String department);

    @Operation(summary = "get user activities")
    @GetMapping("activities")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    List<UserActivityResponse> getActivities(@RequestParam(name = "userId", required = false) String userId);

    @Operation(summary = "get all departments")
    @GetMapping("departments")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER})
    List<String> getDepartments();

    @Operation(summary = "impersonate a user")
    @PostMapping("impersonate/{userId}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void impersonateUser(
            @PathVariable("userId") String userId);

    @Operation(summary = "stop impersonation")
    @PostMapping("stop-impersonation")
    @RolesAllowed({Permissions.IS_ADMIN})
    void stopImpersonation();

    @Operation(summary = "get current user profile")
    @GetMapping("get-profile")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    UserResponse getProfile();

    @Operation(summary = "Send verification code for two-factor authentication setup", 
               description = "Sends a 6-digit verification code via EMAIL or SMS to verify ownership before enabling 2FA. " +
                           "Code expires in 10 minutes. Rate limited to prevent abuse.")
    @PostMapping("send-verification-code")
    @RateLimit(limit = 5, window = 15, timeUnit = TimeUnit.MINUTES, keyStrategy = RateLimitKeyStrategy.USER,
               message = "Too many verification code requests. Please try again in 15 minutes.")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    void sendVerificationCode(@RequestParam("method") String method);

    @Operation(summary = "Verify two-factor authentication code and enable 2FA", 
               description = "Verifies the 6-digit code sent via send-verification-code and enables 2FA for the specified method. " +
                           "After successful verification, 2FA is enabled and backup codes should be generated.")
    @PostMapping("verify-two-step-code")
    @RateLimit(limit = 10, window = 15, timeUnit = TimeUnit.MINUTES, keyStrategy = RateLimitKeyStrategy.USER,
               message = "Too many verification attempts. Please try again in 15 minutes.")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    void verifyTwoStepCode(@RequestParam("code") String code, @RequestParam("method") String method);

    @Operation(summary = "disable two-step verification")
    @PostMapping("disable-two-step-verification")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    void disableTwoStepVerification();

    @Operation(summary = "generate 2FA backup codes")
    @PostMapping("generate-backup-codes")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<String> generateBackupCodes();

    @Operation(summary = "verify and use a backup code")
    @PostMapping("verify-backup-code")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    boolean verifyBackupCode(@RequestParam("code") String code);

    @Operation(summary = "get remaining backup codes count")
    @GetMapping("backup-codes/count")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_USER, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    int getRemainingBackupCodesCount();

    @Operation(summary = "Admin force unlock user account")
    @PostMapping("admin/unlock-account/{userId}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void adminUnlockAccount(@PathVariable("userId") String userId);

    @Operation(summary = "Export user activities to CSV")
    @GetMapping("admin/activities/export")
    @RolesAllowed({Permissions.IS_ADMIN})
    String exportActivitiesCsv(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate);

    @Operation(summary = "Search user activities")
    @GetMapping("admin/activities/search")
    @RolesAllowed({Permissions.IS_ADMIN})
    Page<UserActivityResponse> searchActivities(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Perform bulk operation on users")
    @PostMapping("admin/bulk-operation")
    @RolesAllowed({Permissions.IS_ADMIN})
    BulkOperationResult bulkUserOperation(@RequestBody BulkUserOperationRequest request);

    @Operation(summary = "Get current user's active sessions")
    @GetMapping("sessions")
    List<UserSessionResponse> getMySessions();

    @Operation(summary = "Revoke a specific session")
    @DeleteMapping("sessions/{sessionId}")
    void revokeSession(@PathVariable("sessionId") String sessionId);

    @Operation(summary = "Revoke all sessions except current")
    @DeleteMapping("sessions/revoke-all")
    void revokeAllOtherSessions();
}
