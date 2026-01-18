package com.zote.notification.service.infrastructure.adapters.events;

import com.zote.common.utils.enums.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder for email template contexts.
 *
 * Design Pattern: Builder Pattern + Fluent Interface
 * - Provides a clean, readable API for building template contexts
 * - Handles null safety and default values
 * - Centralizes URL and constant management
 *
 * Benefits:
 * - Type-safe context building
 * - Reusable across all event handlers
 * - Easy to test and maintain
 */
@Component
public class TemplateContextBuilder {

    @Value("${notification.admin-portal.url}")
    private String adminUrlHost;

    @Value("${notification.admin-portal.password-reset-path}")
    private String adminSetPasswordPath; // For admin-created users (initial password setup)

    @Value("${notification.admin-portal.password-reset-request-path:/reset-password}")
    private String adminPasswordResetPath; // For user-initiated password reset requests

    @Value("${notification.admin-portal.email-verification-path:/verify-email}")
    private String adminEmailVerificationPath; // For email verification

    @Value("${notification.admin-portal.privacy-policy-path}")
    private String privacyPolicyPath;

    @Value("${notification.admin-portal.terms-path}")
    private String termsPath;

    @Value("${notification.customer-portal.url:http://localhost:3000}")
    private String customerPortalUrl;
    
    @Value("${notification.customer-portal.password-reset-path:/reset-password}")
    private String customerPasswordResetPath;
    
    @Value("${notification.customer-portal.email-verification-path:/verify-email}")
    private String customerEmailVerificationPath; // For email verification

    /**
     * Start building a new context
     */
    public ContextBuilder newContext() {
        return new ContextBuilder();
    }

    /**
     * Fluent builder for template contexts
     */
    public class ContextBuilder {
        private final Map<String, Object> context = new HashMap<>();

        /**
         * Add user information
         */
        public ContextBuilder withUser(String firstName, String lastName, String email) {
            context.put("firstName", orEmpty(firstName));
            context.put("lastName", orEmpty(lastName));
            context.put("email", orEmpty(email));
            return this;
        }

        /**
         * Add user ID
         */
        public ContextBuilder withUserId(String userId) {
            context.put("userId", orEmpty(userId));
            return this;
        }

        /**
         * Add language
         */
        public ContextBuilder withLanguage(Language language) {
            context.put("language", language != null ? language.getCode() : "en");
            return this;
        }

        /**
         * Add optional user details
         */
        public ContextBuilder withOptionalDetails(String phoneNumber, String branchId, String department) {
            if (phoneNumber != null) context.put("phoneNumber", phoneNumber);
            if (branchId != null) context.put("branchId", branchId);
            if (department != null) context.put("department", department);
            return this;
        }

        /**
         * Add password reset information for admin-created users (initial password setup)
         * Uses /set-password path on admin portal
         * @param token The password reset token
         * @param expiryHours Token expiry time in hours
         */
        public ContextBuilder withPasswordReset(String token, int expiryHours) {
            String resetUrl = adminUrlHost + adminSetPasswordPath + "?token=" + token;
            context.put("passwordResetUrl", resetUrl);
            context.put("passwordResetToken", token);
            context.put("tokenExpiryHours", expiryHours);
            return this;
        }

        /**
         * Add password reset request information (for user-initiated password resets)
         * Routes to appropriate portal based on user type
         * @param token The password reset token
         * @param expiryHours Token expiry time in hours
         * @param isCustomer If true, uses customer portal URL; otherwise uses admin portal URL
         */
        public ContextBuilder withPasswordResetRequest(String token, int expiryHours, boolean isCustomer) {
            String resetUrl;
            if (isCustomer) {
                resetUrl = customerPortalUrl + customerPasswordResetPath + "?token=" + token;
            } else {
                resetUrl = adminUrlHost + adminPasswordResetPath + "?token=" + token;
            }
            context.put("passwordResetUrl", resetUrl);
            context.put("passwordResetToken", token);
            context.put("tokenExpiryHours", expiryHours);
            context.put("isCustomer", isCustomer);
            return this;
        }

        /**
         * Add email verification information (for user-initiated email verification)
         * Routes to appropriate portal based on user type
         * @param token The email verification token
         * @param expiryHours Token expiry time in hours
         * @param isCustomer If true, uses customer portal URL; otherwise uses admin portal URL
         */
        public ContextBuilder withEmailVerification(String token, int expiryHours, boolean isCustomer) {
            String verificationUrl;
            if (isCustomer) {
                verificationUrl = customerPortalUrl + customerEmailVerificationPath + "?token=" + token;
            } else {
                verificationUrl = adminUrlHost + adminEmailVerificationPath + "?token=" + token;
            }
            context.put("emailVerificationUrl", verificationUrl);
            context.put("verificationToken", token);
            context.put("tokenExpiryHours", expiryHours);
            context.put("isCustomer", isCustomer);
            return this;
        }

        /**
         * Add account unlock information
         */
        public ContextBuilder withAccountUnlock(String unlockToken) {
            String unlockUrl = customerPortalUrl + "/auth/unlock-account?token=" + unlockToken;
            context.put("unlockUrl", unlockUrl);
            context.put("unlockToken", unlockToken);
            return this;
        }

        /**
         * Add security event details
         */
        public ContextBuilder withSecurityDetails(int failedAttempts, String ipAddress, LocalDateTime timestamp) {
            context.put("failedAttempts", failedAttempts);
            if (ipAddress != null) context.put("ipAddress", ipAddress);
            if (timestamp != null) context.put("attemptedAt", timestamp.toString());
            return this;
        }

        /**
         * Add account lock details
         */
        public ContextBuilder withLockDetails(LocalDateTime lockedAt, LocalDateTime unlockAt) {
            if (lockedAt != null) context.put("lockedAt", lockedAt.toString());
            if (unlockAt != null) context.put("unlockAt", unlockAt.toString());
            return this;
        }

        /**
         * Add password change details
         */
        public ContextBuilder withPasswordChange(LocalDateTime changedAt, String changedBy, String ipAddress) {
            if (changedAt != null) context.put("changedAt", changedAt.toString());
            context.put("changedBy", orDefault(changedBy, "USER"));
            if (ipAddress != null) context.put("ipAddress", ipAddress);
            context.put("accountUrl", customerPortalUrl + "/account/security");
            return this;
        }

        /**
         * Add suspension reason
         */
        public ContextBuilder withSuspensionReason(String reason) {
            if (reason != null) context.put("reason", reason);
            return this;
        }

        /**
         * Add account URL
         */
        public ContextBuilder withAccountUrl(String path) {
            context.put("accountUrl", customerPortalUrl + path);
            return this;
        }

        /**
         * Add change password URL
         */
        public ContextBuilder withChangePasswordUrl() {
            context.put("changePasswordUrl", customerPortalUrl + "/auth/change-password");
            return this;
        }

        /**
         * Add system constants (company name, support email, etc.)
         */
        public ContextBuilder withSystemConstants() {
            context.put("companyName", "LZ Insurance");
            context.put("supportEmail", "support@lz-insurance.com");
            context.put("supportPhone", "+237 123 456 789");
            context.put("websiteUrl", "https://lz-insurance.com");
            context.put("currentYear", LocalDate.now().getYear());
            return this;
        }

        /**
         * Add footer URLs (privacy policy, terms)
         */
        public ContextBuilder withFooter() {
            context.put("privacyPolicyUrl", adminUrlHost + privacyPolicyPath);
            context.put("termsUrl", adminUrlHost + termsPath);
            context.put("privacyUrl", adminUrlHost + privacyPolicyPath);
            return this;
        }

        /**
         * Add custom field
         */
        public ContextBuilder with(String key, Object value) {
            if (value != null) context.put(key, value);
            return this;
        }

        /**
         * Build the final context map
         */
        public Map<String, Object> build() {
            return new HashMap<>(context);
        }

        private String orEmpty(String value) {
            return value != null ? value : "";
        }

        private String orDefault(String value, String defaultValue) {
            return value != null ? value : defaultValue;
        }
    }
}