package com.zote.user.service.api.controller;

import com.zote.common.utils.ratelimit.RateLimit;
import com.zote.common.utils.ratelimit.RateLimitKeyStrategy;
import com.zote.user.service.api.request.AuthRequest;
import com.zote.user.service.api.request.PasswordResetRequest;
import com.zote.user.service.api.request.RequestPasswordResetRequest;
import com.zote.user.service.api.response.AuthResponse;
import com.zote.user.service.api.response.PasswordPolicyResponse;
import com.zote.user.service.api.response.PasswordStrengthResponse;
import com.zote.user.service.api.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "Auth API")
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface AuthenticationApi {

    @Operation(summary = "Authenticate a user")
    @PostMapping("authenticate")
    @RateLimit(limit = 5, window = 5, timeUnit = TimeUnit.MINUTES, keyStrategy = RateLimitKeyStrategy.IP, 
               message = "Too many login attempts. Please try again in 5 minutes.")
    AuthResponse login(@RequestBody AuthRequest request);

    @Operation(summary = "Refresh access token")
    @PostMapping("auth/refresh-token")
    AuthResponse refreshToken(@RequestParam("refreshToken") String refreshToken);

    @Operation(summary = "Logout current user")
    @PostMapping("auth/logout")
    void logout();

    @Operation(summary = "Get current authenticated user")
    @GetMapping("auth/me")
    UserResponse getCurrentUser();

    @Operation(summary = "Request password reset")
    @PostMapping("auth/forgot-password")
    @RateLimit(limit = 3, window = 15, timeUnit = TimeUnit.MINUTES, keyStrategy = RateLimitKeyStrategy.IP,
               message = "Too many password reset requests. Please try again in 15 minutes.")
    void requestPasswordReset(@RequestBody RequestPasswordResetRequest request);

    @Operation(summary = "Reset password with token")
    @PostMapping("auth/reset-password")
    void resetPassword(@RequestBody PasswordResetRequest request);

    @Operation(summary = "Verify email with token")
    @PostMapping("auth/verify-email")
    void verifyEmail(@RequestParam("token") String token);

    @Operation(summary = "Resend verification email")
    @PostMapping("auth/resend-verification")
    @RateLimit(limit = 3, window = 1, timeUnit = TimeUnit.HOURS, keyStrategy = RateLimitKeyStrategy.IP,
               message = "Too many verification email requests. You can request up to 3 times per hour.")
    void resendVerificationEmail(@RequestParam("email") String email);

    @Operation(summary = "Unlock account with token")
    @PostMapping("auth/unlock-account")
    void unlockAccount(@RequestParam("token") String token);

    @Operation(summary = "Validate password strength")
    @PostMapping("auth/validate-password")
    PasswordStrengthResponse validatePassword(@RequestParam("password") String password);

    @Operation(summary = "Get current password policy")
    @GetMapping("auth/password-policy")
    PasswordPolicyResponse getPasswordPolicy();
}
