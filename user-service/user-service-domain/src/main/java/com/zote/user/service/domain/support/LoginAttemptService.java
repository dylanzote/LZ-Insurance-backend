package com.zote.user.service.domain.support;

import com.zote.common.utils.enums.Status;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepositoryPort userRepositoryPort;
    private final MessagingSupport messagingSupport;
    private final TokenService tokenService;
    private final ActivityLogger activityLogger;

    @Transactional
    public void handleFailedLogin(User user) {
        incrementFailedAttempts(user);

        if (shouldSendNotification(user.getFailedLoginAttempts())) {
            sendFailedLoginNotification(user);
        }

        if (shouldLockAccount(user.getFailedLoginAttempts())) {
            lockAccount(user);
        }
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            userRepositoryPort.saveUser(user);
            log.info("Reset failed login attempts for user {}", user.getEmail());
        }
    }

    @Transactional
    public void clearExpiredAccountLock(User user) {
        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepositoryPort.saveUser(user);
            log.info("Account lock expired for user {}, resetting failed attempts", user.getEmail());
        }
    }

    public void validateAccountStatus(User user) {
        if (user.getStatus() == Status.SUSPENDED) {
            log.warn("Suspended user {} attempted to login", user.getEmail());
            throw new FunctionalError(SecurityConstants.ERROR_ACCOUNT_SUSPENDED);
        }

        if (isAccountLocked(user)) {
            log.warn("Account locked for user {} until {}", user.getEmail(), user.getAccountLockedUntil());
            throw new FunctionalError(SecurityConstants.ERROR_ACCOUNT_LOCKED);
        }
    }

    private void incrementFailedAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        userRepositoryPort.saveUser(user);
        log.warn("Failed login attempt #{} for user {}", user.getFailedLoginAttempts(), user.getEmail());
    }

    private boolean shouldSendNotification(int failedAttempts) {
        return failedAttempts == SecurityConstants.NOTIFICATION_THRESHOLD;
    }

    private boolean shouldLockAccount(int failedAttempts) {
        return failedAttempts >= SecurityConstants.MAX_FAILED_LOGIN_ATTEMPTS;
    }

    private boolean isAccountLocked(User user) {
        return user.getAccountLockedUntil() != null &&
               user.getAccountLockedUntil().isAfter(LocalDateTime.now());
    }

    private void sendFailedLoginNotification(User user) {
        log.info("Sending failed login notification to user {} after {} attempts", user.getEmail(), SecurityConstants.NOTIFICATION_THRESHOLD);

        messagingSupport.publishFailedLoginEvent(user, user.getFailedLoginAttempts(), null, null);
        activityLogger.logActivity(
            user.getId(),
            SecurityConstants.ACTIVITY_FAILED_LOGIN_ALERT,
            SecurityConstants.MODULE_AUTH,
            "Failed attempts: " + user.getFailedLoginAttempts()
        );
    }

    private void lockAccount(User user) {
        user.setAccountLockedUntil(LocalDateTime.now().plusHours(SecurityConstants.ACCOUNT_LOCK_DURATION_HOURS));
        userRepositoryPort.saveUser(user);
        log.error("Account locked for user {} due to {} failed login attempts", user.getEmail(), user.getFailedLoginAttempts());

        String unlockToken = generateUnlockToken(user);
        publishAccountLockedEvent(user, unlockToken);

        activityLogger.logActivity(
            user.getId(),
            SecurityConstants.ACTIVITY_ACCOUNT_LOCKED,
            SecurityConstants.MODULE_AUTH,
            "Failed attempts: " + user.getFailedLoginAttempts()
        );
    }

    private String generateUnlockToken(User user) {
        var unlockToken = tokenService.generatePasswordResetToken(user);
        return unlockToken.getToken();
    }

    private void publishAccountLockedEvent(User user, String unlockToken) {
        messagingSupport.publishAccountLockedEvent(
            user,
            user.getFailedLoginAttempts(),
            null,
            null,
            unlockToken
        );
    }
}
