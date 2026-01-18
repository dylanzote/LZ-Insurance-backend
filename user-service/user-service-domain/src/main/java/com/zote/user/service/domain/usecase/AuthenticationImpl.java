package com.zote.user.service.domain.usecase;

import com.zote.common.utils.config.BeanConfig;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.enums.TokenType;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.utils.SecurityUtils;
import com.zote.user.service.domain.model.AuthData;
import com.zote.user.service.domain.model.PasswordStrength;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.inbound.AuthenticationPort;
import com.zote.keycloak.adapter.KeyCloakService;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import com.zote.user.service.domain.support.*;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationImpl implements AuthenticationPort {

    private final UserRepositoryPort userRepositoryPort;

    private final BeanConfig config;

    private final UserSupport userSupport;
    
    private final ActivityLogger activityLogger;
    
    private final TokenService tokenService;
    
    private final MessagingSupport messagingSupport;

    private final KeyCloakService keyCloakService;
    
    private final PasswordPolicyService passwordPolicyService;

    private final LoginAttemptService loginAttemptService;

    private final UserProfileService userProfileService;

    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    @Override
    public AuthData authenticate(String email, String password) {
        log.info("incoming Authentication request for {}", email);
        var user = userRepositoryPort.findUserByEmail(email);
        loginAttemptService.validateAccountStatus(user);
        loginAttemptService.clearExpiredAccountLock(user);
        validateUserCredentials(user, password);
        loginAttemptService.resetFailedAttempts(user);
        var authData = authenticateWithKeycloak(email, password);
        User updatedUser = processSuccessfulLogin(user);
        authData.setUser(updatedUser);
        validatePasswordChangeRequirement(updatedUser);
        return authData;
    }


    @Override
    public AuthData refreshToken(String refreshToken) {
        log.info("Refreshing user token token");
        try {
            var refreshTokenRequest = userSupport.buildRefreshRequest(refreshToken);
            return userSupport.authenticateKeycloakUser(refreshTokenRequest);
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new FunctionalError(SecurityConstants.ERROR_REFRESH_TOKEN_FAILED + e.getMessage());
        }
    }

    @Override
    public User getCurrentUser() {
        log.info("Fetching current authenticated user");
        var keycloakUserId = SecurityUtils.getCurrentUsername();
        return userRepositoryPort.findUserByKeyCloakId(keycloakUserId);
    }

    @Override
    public void logout() {
        log.info("Processing user logout");
        try {
            var user = getCurrentUser();
            activityLogger.logActivityAsync(user.getId(), SecurityConstants.ACTIVITY_LOGOUT, SecurityConstants.MODULE_AUTH, null);
        } catch (Exception e) {
            log.warn("Could not log logout activity: {}", e.getMessage());
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    public void requestPasswordReset(String email) {
        log.info("Requesting password reset for email: {}", email);
        var user = userRepositoryPort.findUserByEmail(email);
        var token = tokenService.generatePasswordResetToken(user);
        messagingSupport.publishRequestPasswordResetEvent(user, token.getToken(), token.getExpiresAt().getHour());
        activityLogger.logActivity(user.getId(), "request_password_reset", SecurityConstants.MODULE_AUTH, null);
    }

    @Override
    public void resetPassword(String token, String newPassword, String twoFactorCode) {
        log.info("Resetting password with token");
        if (!tokenService.validateToken(token, TokenType.PASSWORD_RESET)) {
            throw new FunctionalError("Invalid or expired reset token");
        }
        var userToken = tokenService.getToken(token, TokenType.PASSWORD_RESET);
        var user = userRepositoryPort.findUserById(userToken.getUserId());

        twoFactorAuthenticationService.verifyTwoFactorEnabled(user, twoFactorCode);
        userSupport.validatePasswords(newPassword);
        keyCloakService.resetPassword(user.getKeycloakUserId(), newPassword);

        user.setPassword(config.passwordEncoder().encode(newPassword));
        if (user.isMustChangePassword()) {
            user.setMustChangePassword(false);
            log.info("Cleared mustChangePassword flag for user: {} after password reset", user.getId());
        }
        user.setPasswordChangedAt(LocalDateTime.now());
        log.info("Updated passwordChangedAt for user: {}", user.getId());
        userRepositoryPort.saveUser(user);

        tokenService.markTokenAsUsed(token);
        activityLogger.logActivity(user.getId(), SecurityConstants.ACTIVITY_PASSWORD_RESET, SecurityConstants.MODULE_AUTH, null);
        messagingSupport.publishPasswordChangedEvent(user, "USER", null, null);
    }

    @Override
    public void verifyEmail(String token) {
        log.info("Verifying email with token");
        if (!tokenService.validateToken(token, TokenType.EMAIL_VERIFICATION)) {
            throw new FunctionalError("Invalid or expired verification token");
        }
        
        var userToken = tokenService.getToken(token, TokenType.EMAIL_VERIFICATION);
        var user = userRepositoryPort.findUserById(userToken.getUserId());
        user.setEmailConfirmed(true);
        user.setStatus(Status.ACTIVE);
        userRepositoryPort.saveUser(user);

        tokenService.markTokenAsUsed(token);
        activityLogger.logActivity(user.getId(), SecurityConstants.ACTIVITY_EMAIL_VERIFICATION, SecurityConstants.MODULE_AUTH, null);
    }

    @Override
    public void resendVerificationEmail(String email) {
        log.info("Resending verification email to: {}", email);
        var user = userRepositoryPort.findUserByEmail(email);
        if (user.isEmailConfirmed()) {
            throw new FunctionalError(SecurityConstants.ERROR_EMAIL_ALREADY_VERIFIED);
        }
        var token = tokenService.generateEmailVerificationToken(user);
        messagingSupport.publishEmailVerificationRequestEvent(user, token.getToken(), token.getExpiresAt().getHour());

        activityLogger.logActivity(user.getId(), "resend_verification_email", SecurityConstants.MODULE_AUTH, null);
    }


    @Override
    public void unlockAccount(String token) {
        log.info("Unlocking account with token");

        if (!tokenService.validateToken(token, TokenType.PASSWORD_RESET)) {
            throw new FunctionalError("Invalid or expired unlock token");
        }
        var userToken = tokenService.getToken(token, TokenType.PASSWORD_RESET);
        var user = userRepositoryPort.findUserById(userToken.getUserId());
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepositoryPort.saveUser(user);
        tokenService.markTokenAsUsed(token);
        activityLogger.logActivity(user.getId(), "account_unlocked", SecurityConstants.MODULE_AUTH, "Unlocked via token");
        
        log.info("Account unlocked successfully for user: {}", user.getId());
    }

    @Override
    public PasswordStrength validatePassword(String password) {
        log.info("Validating password strength");
        return passwordPolicyService.validatePassword(password);
    }

    @Override
    public PasswordPolicyService.PasswordPolicyConfig getPasswordPolicy() {
        log.info("Retrieving password policy configuration");
        return passwordPolicyService.getPasswordPolicy();
    }

    private void validateUserCredentials(User user, String password) {
        if (!config.passwordEncoder().matches(password, user.getPassword())) {
            log.error("Invalid credentials for user {}", user.getEmail());
            loginAttemptService.handleFailedLogin(user);
            throw new FunctionalError(SecurityConstants.ERROR_INVALID_CREDENTIALS);
        }
    }

    private AuthData authenticateWithKeycloak(String email, String password) {
        var authRequest = userSupport.buildAuthRequest(email, password);
        return userSupport.authenticateKeycloakUser(authRequest);
    }

    private User processSuccessfulLogin(User user) {
        userProfileService.updateUserLastLogin(user);
        var updatedUser = userRepositoryPort.saveUser(user);
        updatedUser.setImageUrl(userProfileService.fetchUserImageAsync(updatedUser).getNow(null));

        activityLogger.logActivityAsync(
            updatedUser.getId(),
            SecurityConstants.ACTIVITY_LOGIN,
            SecurityConstants.MODULE_AUTH,
            null
        );
        return updatedUser;
    }

    private void validatePasswordChangeRequirement(User user) {
        if (isPasswordChangeRequired(user)) {
            log.warn("User {} must change password before accessing the system", user.getId());
            throw new FunctionalError(SecurityConstants.ERROR_PASSWORD_CHANGE_REQUIRED);
        }
    }

    public boolean isPasswordChangeRequired(User user) {
        return user.isMustChangePassword() && !isCustomer(user);
    }

    private boolean isCustomer(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.isCustomerRole());
    }
}
