package com.zote.user.service.api.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.api.controller.AuthenticationApi;
import com.zote.user.service.api.request.AuthRequest;
import com.zote.user.service.api.request.PasswordResetRequest;
import com.zote.user.service.api.request.RequestPasswordResetRequest;
import com.zote.user.service.api.response.AuthResponse;
import com.zote.user.service.api.response.PasswordPolicyResponse;
import com.zote.user.service.api.response.PasswordStrengthResponse;
import com.zote.user.service.api.response.UserResponse;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.inbound.AuthenticationPort;
import com.zote.user.service.domain.ports.inbound.UserPort;
import com.zote.user.service.domain.support.ActivityLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService implements AuthenticationApi {

    private final AuthenticationPort authenticationPort;
    private final UserPort userPort;
    private final ActivityLogger activityLogger;

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login request for user: {}", request.email());
        return AuthResponse.toAuthResponse(authenticationPort.authenticate(request.email(), request.password()));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refresh token request");
        return AuthResponse.toAuthResponse(authenticationPort.refreshToken(refreshToken));
    }

    @Override
    public void logout() {
        log.info("Logout request");
        authenticationPort.logout();
    }

    @Override
    public UserResponse getCurrentUser() {
        log.info("Get current user request");
        return UserResponse.toResponse(authenticationPort.getCurrentUser());
    }

    @Override
    public void requestPasswordReset(RequestPasswordResetRequest request) {
        log.info("Password reset request for email: {}", request.email());
        authenticationPort.requestPasswordReset(request.email());
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        log.info("Reset password request with token");
        authenticationPort.resetPassword(request.token(), request.newPassword(), request.twoFactorCode());
    }

    @Override
    public void verifyEmail(String token) {
        log.info("Email verification request with token");
        authenticationPort.verifyEmail(token);
    }

    @Override
    public void resendVerificationEmail(String email) {
        log.info("Resend verification email request for: {}", email);
        authenticationPort.resendVerificationEmail(email);
    }

    @Override
    public void unlockAccount(String token) {
        log.info("Unlock account request with token");
        authenticationPort.unlockAccount(token);
    }

    @Override
    public PasswordStrengthResponse validatePassword(String password) {
        log.info("Password strength validation request");
        var strength = authenticationPort.validatePassword(password);
        return PasswordStrengthResponse.builder()
                .valid(strength.isValid())
                .strength(strength.getStrength())
                .score(strength.getScore())
                .suggestions(strength.getSuggestions())
                .requirements(strength.getRequirements())
                .meetsMinimumRequirements(strength.isMeetsMinimumRequirements())
                .build();
    }

    @Override
    public PasswordPolicyResponse getPasswordPolicy() {
        log.info("Password policy request");
        var policy = authenticationPort.getPasswordPolicy();
        return PasswordPolicyResponse.builder()
                .minLength(policy.minLength())
                .maxLength(policy.maxLength())
                .requireUppercase(policy.requireUppercase())
                .requireLowercase(policy.requireLowercase())
                .requireDigit(policy.requireDigit())
                .requireSpecialChar(policy.requireSpecialChar())
                .passwordHistoryCount(policy.passwordHistoryCount())
                .maxAgeDays(policy.maxAgeDays())
                .preventCommonPasswords(policy.preventCommonPasswords())
                .build();
    }
}
