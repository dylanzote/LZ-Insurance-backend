package com.zote.user.service.domain.support;

public class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    // Login attempt constants
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final int NOTIFICATION_THRESHOLD = 3;
    public static final int ACCOUNT_LOCK_DURATION_HOURS = 1;

    // Error codes
    public static final String ERROR_ACCOUNT_SUSPENDED = "ACCOUNT_SUSPENDED";
    public static final String ERROR_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String ERROR_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ERROR_PASSWORD_CHANGE_REQUIRED = "PASSWORD_CHANGE_REQUIRED";
    public static final String ERROR_REFRESH_TOKEN_FAILED = "REFRESH_TOKEN_FAILED";
    public static final String ERROR_INVALID_RESET_TOKEN = "INVALID_OR_EXPIRED_RESET_TOKEN";
    public static final String ERROR_INVALID_VERIFICATION_TOKEN = "INVALID_OR_EXPIRED_VERIFICATION_TOKEN";
    public static final String ERROR_EMAIL_ALREADY_VERIFIED = "EMAIL_ALREADY_VERIFIED";
    public static final String ERROR_INVALID_DATE_FORMAT = "ERROR_INVALID_DATE_FORMAT";
    public static final String ERROR_EXPORT_RECORD_LIMIT_EXCEEDED = "ERROR_EXPORT_RECORD_LIMIT_EXCEEDED";
    public static final String ERROR_INVALID_DATE_RANGE = "ERROR_INVALID_DATE_RANGE";

    // URL templates
    public static final String RESET_PASSWORD_URL_TEMPLATE = "http://localhost:5173/auth/reset-password?token=%s";
    public static final String VERIFY_EMAIL_URL_TEMPLATE = "http://localhost:4200/auth/verify-email?token=%s";

    // Activity types
    public static final String ACTIVITY_LOGIN = "login";
    public static final String ACTIVITY_LOGOUT = "logout";
    public static final String ACTIVITY_PASSWORD_RESET_REQUEST = "request_password_reset";
    public static final String ACTIVITY_PASSWORD_RESET = "reset_password";
    public static final String ACTIVITY_EMAIL_VERIFICATION = "verify_email";
    public static final String ACTIVITY_RESEND_VERIFICATION = "resend_verification_email";
    public static final String ACTIVITY_FAILED_LOGIN_ALERT = "failed_login_alert";
    public static final String ACTIVITY_ACCOUNT_LOCKED = "account_locked";
    public static final String ACTIVITY_ACCOUNT_UNLOCKED = "account_unlocked";
    public static final String ACTIVITY_ENABLE_2FA = "enable_two_factor";
    public static final String ACTIVITY_DISABLE_2FA = "disable_two_factor";
    public static final String ACTIVITY_VERIFY_2FA_SUCCESS = "verify_two_factor_success";
    public static final String ACTIVITY_VERIFY_2FA_FAILED = "verify_two_factor_failed";
    public static final String ACTIVITY_SEND_2FA_CODE = "send_two_factor_verification_code";
    public static final String ACTIVITY_SEND_2FA_VERIFICATION_CODE = "send_two_factor_verification_code";
    public static final String ACTIVITY_ENABLE_2FA_VERIFIED = "two_factor_enabled";
    public static final String ACTIVITY_GENERATE_BACKUP_CODES = "generate_backup_codes";
    public static final String ACTIVITY_USE_BACKUP_CODE = "use_backup_code";
    public static final String ACTIVITY_REVOKE_BACKUP_CODES = "revoke_backup_codes";
    
    // Sensitive Operation 2FA verification
    public static final String ACTIVITY_SEND_2FA_CODE_FOR_OPERATION = "send_2fa_code_for_operation";
    public static final String ACTIVITY_VERIFY_2FA_CODE_FOR_OPERATION_SUCCESS = "verify_2fa_code_for_operation_success";
    public static final String ACTIVITY_VERIFY_2FA_CODE_FOR_OPERATION_FAILED = "verify_2fa_code_for_operation_failed";

     // Two-Factor Authentication constants
    public static final String ERROR_USER_ID_REQUIRED = "USER_ID_REQUIRED";
    public static final String ERROR_2FA_METHOD_REQUIRED = "2FA_METHOD_REQUIRED";
    public static final String ERROR_2FA_ALREADY_ENABLED = "2FA_ALREADY_ENABLED";
    public static final String ERROR_2FA_NOT_ENABLED = "2FA_NOT_ENABLED";
    public static final String ERROR_EMAIL_REQUIRED_FOR_2FA = "EMAIL_REQUIRED_FOR_2FA";
    public static final String ERROR_PHONE_REQUIRED_FOR_2FA = "PHONE_REQUIRED_FOR_2FA";
    public static final String ERROR_EMAIL_NOT_VERIFIED_FOR_2FA = "EMAIL_NOT_VERIFIED_FOR_2FA";
    public static final String ERROR_UNSUPPORTED_2FA_METHOD = "UNSUPPORTED_2FA_METHOD";
    public static final String ERROR_2FA_VERIFICATION_REQUIRED = "2FA_VERIFICATION_REQUIRED";
    public static final String ERROR_INVALID_2FA_CODE = "INVALID_2FA_CODE";
    public static final String ERROR_INVALID_2FA_CODE_FORMAT = "INVALID_2FA_CODE_FORMAT";

    // Impersonation
    public static final String ERROR_IMPERSONATION_PERMISSION_DENIED = "IMPERSONATION_PERMISSION_DENIED";
    public static final String ERROR_SELF_IMPERSONATION = "SELF_IMPERSONATION_NOT_ALLOWED";
    public static final String ERROR_PRIVILEGE_ESCALATION = "PRIVILEGE_ESCALATION_NOT_ALLOWED";
    public static final String ACTIVITY_IMPERSONATE = "impersonate";
    public static final String ACTIVITY_IMPERSONATED_BY = "impersonated_by";

    // Backup Codes
    public static final String ERROR_2FA_NOT_ENABLED_FOR_BACKUP_CODES = "2FA_NOT_ENABLED_FOR_BACKUP_CODES";
    public static final String ERROR_BACKUP_CODE_GENERATION_FAILED = "BACKUP_CODE_GENERATION_FAILED";
    public static final String ERROR_BACKUP_CODE_VERIFICATION_FAILED = "BACKUP_CODE_VERIFICATION_FAILED";
    public static final String ERROR_INVALID_BACKUP_CODE_FORMAT = "INVALID_BACKUP_CODE_FORMAT";
    public static final String ACTIVITY_ADMIN_GENERATE_BACKUP_CODES = "admin_generate_backup_codes";
    public static final String ACTIVITY_ADMIN_REVOKE_BACKUP_CODES = "admin_revoke_backup_codes";
    public static final String ERROR_ADMIN_PERMISSION_REQUIRED = "ADMIN_PERMISSION_REQUIRED";

    // Module names
    public static final String MODULE_AUTH = "auth";
    public static final String MODULE_USER = "user";
    public static final String MODULE_PROFILE = "profile";
}
