package com.zote.user.service.domain.support;

import com.zote.common.utils.enums.TokenType;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthenticationService {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenService tokenService;
    private final MessagingSupport messagingSupport;
    private final ActivityLogger activityLogger;

    @Transactional
    public void enableTwoFactor(String userId, TwoFacMethod method) {
        log.info("Enabling two-factor authentication for user: {} with method: {}", userId, method);
        validateUserId(userId);
        var user = findUserById(userId);
        validateEnableTwoFactor(user, method);
        enableTwoFactorForUser(user, method);
        logActivity(userId, SecurityConstants.ACTIVITY_ENABLE_2FA, method);
    }

    @Transactional
    public void disableTwoFactor(User user) {
        log.info("Disabling two-factor authentication for user: {}", user.getId());
        validateDisableTwoFactor(user);
        disableTwoFactorForUser(user);
        logActivity(user.getId(), SecurityConstants.ACTIVITY_DISABLE_2FA, null);
    }

    public void verifyTwoFactorEnabled(User user, String twoFactorCode) {
        if (user.isTwoFactorEnabled()) {
            if (twoFactorCode == null || twoFactorCode.trim().isEmpty()) {
                throw new FunctionalError(SecurityConstants.ERROR_2FA_VERIFICATION_REQUIRED +
                                         " - Two-factor authentication code is required. Please request a code using /user/send-verification-code?method=" + user.getTwoFactorMethod());
            }
            boolean isValid = verifyTwoFactorCode(user.getId(), twoFactorCode);
            if (!isValid) {
                throw new FunctionalError(SecurityConstants.ERROR_INVALID_2FA_CODE);
            }
        }
    }

    public boolean verifyTwoFactorCode(String userId, String code) {
        log.info("Verifying two-factor code for user: {}", userId);

        if (!isValidVerificationRequest(userId, code)) {
            log.warn("Invalid verification attempt - userId or code is null/empty");
            return false;
        }
        boolean isValid = validateTwoFactorCode(userId, code);
        if (isValid) {
            markTokenAsUsed(userId, code);
            logActivity(userId, SecurityConstants.ACTIVITY_VERIFY_2FA_SUCCESS, null);
        } else {
            logActivity(userId, SecurityConstants.ACTIVITY_VERIFY_2FA_FAILED, null);
        }
        return isValid;
    }

    @Transactional
    public void verifyAndEnableTwoFactor(User user, String code, TwoFacMethod method) {
        log.info("Verifying and enabling 2FA for user: {} with method: {}", user.getId(), method);

        validateVerificationCodeFormat(code);
        if (user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_ALREADY_ENABLED);
        }
        boolean codeIsValid = verifyTwoFactorCode(user.getId(), code);
        if (!codeIsValid) {
            log.warn("Invalid verification code for user: {}", user.getId());
            throw new FunctionalError(SecurityConstants.ERROR_INVALID_2FA_CODE);
        }
        validateEnableTwoFactor(user, method);
        enableTwoFactorForUser(user, method);
        logActivity(user.getId(), SecurityConstants.ACTIVITY_ENABLE_2FA_VERIFIED, method);
    }

    @Transactional
    public String sendTwoFactorCode(User user, TwoFacMethod method) {
        log.info("Sending two-factor verification code to user: {} via method: {}", user.getId(), method);

        if (user.isTwoFactorEnabled()) {
            TwoFacMethod userMethod = user.getTwoFactorMethod();
            if (userMethod == null) {
                throw new FunctionalError("Two-factor authentication method is not configured");
            }
            method = userMethod;
        } else {
            validateTwoFactorMethod(method);
            validateSendTwoFactorCode(user, method);
        }

        if (method == TwoFacMethod.EMAIL) {
            if (!StringUtils.hasText(user.getEmail())) {
                throw new FunctionalError(SecurityConstants.ERROR_EMAIL_REQUIRED_FOR_2FA);
            }
        } else if (method == TwoFacMethod.SMS && !StringUtils.hasText(user.getPhoneNumber())) {
                throw new FunctionalError(SecurityConstants.ERROR_PHONE_REQUIRED_FOR_2FA);
        }

        String code = generateAndStoreTwoFactorCode(user);
        sendCodeToUser(user, code, method);
        logActivity(user.getId(), SecurityConstants.ACTIVITY_SEND_2FA_CODE, method);
        return code;
    }

    @Transactional
    public String sendTwoFactorVerificationCode(String userId, TwoFacMethod method) {
        log.info("Sending 2FA verification code for enabling 2FA - user: {}, method: {}", userId, method);

        validateUserId(userId);
        User user = findUserById(userId);

        if (user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_ALREADY_ENABLED);
        }

        validateEnableTwoFactor(user, method);
        String code = generateAndStoreTwoFactorCode(user);
        sendCodeToUser(user, code, method);
        logActivity(userId, SecurityConstants.ACTIVITY_SEND_2FA_VERIFICATION_CODE, method);

        return code;
    }




    private void validateUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new FunctionalError(SecurityConstants.ERROR_USER_ID_REQUIRED);
        }
    }

    private void validateTwoFactorMethod(TwoFacMethod method) {
        if (method == null) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_METHOD_REQUIRED);
        }
    }

    private void validateEnableTwoFactor(User user, TwoFacMethod method) {
        if (user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_ALREADY_ENABLED);
        }

        switch (method) {
            case EMAIL:
                validateEmailForTwoFactor(user);
                break;
            case SMS:
                validatePhoneForTwoFactor(user);
                break;
            default:
                throw new FunctionalError(String.format(SecurityConstants.ERROR_UNSUPPORTED_2FA_METHOD, method));
        }
    }

    private void validateEmailForTwoFactor(User user) {
        if (!StringUtils.hasText(user.getEmail())) {
            throw new FunctionalError(SecurityConstants.ERROR_EMAIL_REQUIRED_FOR_2FA);
        }

        if (!user.isEmailConfirmed()) {
            throw new FunctionalError(SecurityConstants.ERROR_EMAIL_NOT_VERIFIED_FOR_2FA);
        }
    }

    private void validatePhoneForTwoFactor(User user) {
        if (!StringUtils.hasText(user.getPhoneNumber())) {
            throw new FunctionalError(SecurityConstants.ERROR_PHONE_REQUIRED_FOR_2FA);
        }
    }

    private void validateDisableTwoFactor(User user) {
        if (!user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_NOT_ENABLED);
        }
    }

    private void validateSendTwoFactorCode(User user, TwoFacMethod method) {
        if (user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_ALREADY_ENABLED);
        }

        switch (method) {
            case EMAIL:
                validateEmailForCodeSending(user);
                break;
            case SMS:
                validatePhoneForCodeSending(user);
                break;
            default:
                throw new FunctionalError(String.format(
                    SecurityConstants.ERROR_UNSUPPORTED_2FA_METHOD, method));
        }
    }

    private void validateEmailForCodeSending(User user) {
        if (!StringUtils.hasText(user.getEmail())) {
            throw new FunctionalError(SecurityConstants.ERROR_EMAIL_REQUIRED_FOR_2FA);
        }
    }

    private void validatePhoneForCodeSending(User user) {
        if (!StringUtils.hasText(user.getPhoneNumber())) {
            throw new FunctionalError(SecurityConstants.ERROR_PHONE_REQUIRED_FOR_2FA);
        }
    }

    private boolean isValidVerificationRequest(String userId, String code) {
        return StringUtils.hasText(userId) && StringUtils.hasText(code);
    }

    private User findUserById(String userId) {
        return userRepositoryPort.findUserById(userId);
    }

    private void enableTwoFactorForUser(User user, TwoFacMethod method) {
        user.setTwoFactorEnabled(true);
        user.setTwoFactorMethod(method);
        userRepositoryPort.saveUser(user);
        log.info("Enabled two-factor authentication for user: {} with method: {}", user.getId(), method);
    }

    private void disableTwoFactorForUser(User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorMethod(null);
        user.setTwoFactorSecret(null);
        userRepositoryPort.saveUser(user);
        log.info("Disabled two-factor authentication for user: {}", user.getId());
    }

    private String generateAndStoreTwoFactorCode(User user) {
        String code = tokenService.generateTwoFactorCode();
        tokenService.generateTwoFactorToken(user, code);
        return code;
    }

    private void sendCodeToUser(User user, String code, TwoFacMethod method) {
        messagingSupport.publishTwoFactorCodeEvent(user, code, method);
        log.info("Published 2FA verification code event for user: {} via {}", user.getId(), method);
    }

    private boolean validateTwoFactorCode(String userId, String code) {
        var tokens = tokenService.findActiveTokensByUserAndType(userId, TokenType.TWO_FACTOR_VERIFICATION);
        return tokens.stream()
            .anyMatch(token -> token.getToken().equals(code) && token.isValid());
    }

    private void markTokenAsUsed(String userId, String code) {
        tokenService.markTokenAsUsed(code);
        log.info("Marked two-factor token as used for user: {}", userId);
    }


    private void logActivity(String userId, String activity, TwoFacMethod method) {
        String details = method != null ? "Method: " + method : null;
        activityLogger.logActivity(userId, activity, SecurityConstants.MODULE_PROFILE, details);
    }

    private void validateVerificationCodeFormat(String code) {
        if (code == null || code.length() != 6 || !code.matches("\\d{6}")) {
            throw new FunctionalError(SecurityConstants.ERROR_INVALID_2FA_CODE_FORMAT);
        }
    }


    public boolean isTwoFactorEnabled(String userId) {
        validateUserId(userId);
        User user = findUserById(userId);
        return user.isTwoFactorEnabled();
    }

    public TwoFacMethod getUserTwoFactorMethod(String userId) {
        validateUserId(userId);
        User user = findUserById(userId);
        return user.getTwoFactorMethod();
    }

    public void validateTwoFactorRequirement(User user) {
        if (user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_VERIFICATION_REQUIRED);
        }
    }
}
