package com.zote.user.service.domain.support;

import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.kafka.adapter.event.AccountLockedEvent;
import com.zote.kafka.adapter.event.EmailVerificationRequestEvent;
import com.zote.kafka.adapter.event.FailedLoginEvent;
import com.zote.kafka.adapter.event.PasswordChangedEvent;
import com.zote.kafka.adapter.event.PasswordResetRequestEvent;
import com.zote.kafka.adapter.event.TwoFactorCodeEvent;
import com.zote.kafka.adapter.event.UserActivatedEvent;
import com.zote.kafka.adapter.event.UserCreatedEvent;
import com.zote.kafka.adapter.event.UserSuspendedEvent;
import com.zote.kafka.adapter.event.UserUpdatedEvent;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingSupport {

    private final EventPublisherPort eventPublisher;
    
    /**
     * Publishes USER_CREATED event for self-registered users
     */
    public void publishUserCreatedEvent(User user) {
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .language(user.getLanguage())
                    .branchId(user.getBranchId())
                    .department(user.getDepartment())
                    .createdByAdmin(false)
                    .passwordResetToken(null)
                    .createdByUserId(null)
                    .build();

            eventPublisher.publishUserCreated(event);
            log.info("Published USER_CREATED event for self-registered user: {}", user.getId());

        } catch (Exception e) {
            log.error("Failed to publish USER_CREATED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes USER_CREATED event for admin-created users with password reset token
     */
    public void publishAdminCreatedUserEvent(User user, String passwordResetToken, int resetTokenExpiryTime, String adminUserId) {
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .language(user.getLanguage())
                    .branchId(user.getBranchId())
                    .department(user.getDepartment())
                    .createdByAdmin(true)
                    .passwordResetToken(passwordResetToken)
                    .passwordResetTokenExpiryTime(resetTokenExpiryTime)
                    .createdByUserId(adminUserId)
                    .build();

            eventPublisher.publishUserCreated(event);
            log.info("Published USER_CREATED event for admin-created user: {} by admin: {}", user.getId(), adminUserId);

        } catch (Exception e) {
            log.error("Failed to publish USER_CREATED event for admin-created user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes USER_UPDATED event when user profile is updated
     */
    public void publishUserUpdatedEvent(User user, String updatedFields) {
        try {
            UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .language(user.getLanguage())
                    .updatedFields(updatedFields)
                    .build();

            eventPublisher.publishUserUpdated(event);
            log.info("Published USER_UPDATED event for user: {} (fields: {})", user.getId(), updatedFields);

        } catch (Exception e) {
            log.error("Failed to publish USER_UPDATED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes USER_ACTIVATED event when admin activates a user
     */
    public void publishUserActivatedEvent(User user, String activatedBy) {
        try {
            UserActivatedEvent event = UserActivatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .activatedBy(activatedBy)
                    .build();

            eventPublisher.publishUserActivated(event);
            log.info("Published USER_ACTIVATED event for user: {} by: {}", user.getId(), activatedBy);

        } catch (Exception e) {
            log.error("Failed to publish USER_ACTIVATED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes USER_SUSPENDED event when admin suspends a user
     */
    public void publishUserSuspendedEvent(User user, String suspendedBy, String reason) {
        try {
            UserSuspendedEvent event = UserSuspendedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .suspensionReason(reason)
                    .suspendedBy(suspendedBy)
                    .suspendedUntil(null) // Can be enhanced later for temporary suspensions
                    .build();

            eventPublisher.publishUserSuspended(event);
            log.info("Published USER_SUSPENDED event for user: {} by: {}", user.getId(), suspendedBy);

        } catch (Exception e) {
            log.error("Failed to publish USER_SUSPENDED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes PASSWORD_CHANGED event for security notifications
     */
    public void publishPasswordChangedEvent(User user, String changedBy, String ipAddress, String userAgent) {
        try {
            PasswordChangedEvent event = PasswordChangedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .changedBy(changedBy)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            eventPublisher.publishPasswordChanged(event);
            log.info("Published PASSWORD_CHANGED event for user: {} by: {}", user.getId(), changedBy);

        } catch (Exception e) {
            log.error("Failed to publish PASSWORD_CHANGED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes PASSWORD_RESET_REQUESTED event when user requests password reset
     */
    public void publishRequestPasswordResetEvent(User user, String passwordResetToken, int resetTokenExpiryTime) {
        try {
            // Determine if user is a customer (for routing to correct portal)
            boolean isCustomer = user.getRoles().stream()
                    .anyMatch(role -> role.isCustomerRole());

            PasswordResetRequestEvent event = PasswordResetRequestEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .language(user.getLanguage())
                    .passwordResetToken(passwordResetToken)
                    .passwordResetTokenExpiryTime(resetTokenExpiryTime)
                    .isCustomer(isCustomer)
                    .build();

            eventPublisher.publishPasswordResetRequest(event);
            log.info("Published PASSWORD_RESET_REQUESTED event for user: {} (isCustomer: {})", user.getId(), isCustomer);

        } catch (Exception e) {
            log.error("Failed to publish PASSWORD_RESET_REQUESTED event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes FAILED_LOGIN event for security monitoring
     */
    public void publishFailedLoginEvent(User user, int failedAttempts, String ipAddress, String userAgent) {
        try {
            FailedLoginEvent event = FailedLoginEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .failedAttempts(failedAttempts)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .attemptedAt(LocalDateTime.now())
                    .build();

            eventPublisher.publishFailedLogin(event);
            log.info("Published FAILED_LOGIN event for user: {} (attempts: {})", user.getId(), failedAttempts);

        } catch (Exception e) {
            log.error("Failed to publish FAILED_LOGIN event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes ACCOUNT_LOCKED event when user account is locked due to failed login attempts
     */
    public void publishAccountLockedEvent(User user, int failedAttempts, String ipAddress, String userAgent, String unlockToken) {
        try {
            AccountLockedEvent event = AccountLockedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .failedAttempts(failedAttempts)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .lockedAt(LocalDateTime.now())
                    .unlockAt(LocalDateTime.now().plusHours(1)) // Auto-unlock after 1 hour
                    .unlockToken(unlockToken)
                    .build();

            eventPublisher.publishAccountLocked(event);
            log.info("Published ACCOUNT_LOCKED event for user: {} (failed attempts: {})", user.getId(), failedAttempts);

        } catch (Exception e) {
            log.error("Failed to publish ACCOUNT_LOCKED event for user: {}", user.getId(), e);
        }
    }

    public void publishTwoFactorCodeEvent(User user, String code, TwoFacMethod method) {
        try {
            TwoFactorCodeEvent event = TwoFactorCodeEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .language(user.getLanguage())
                    .code(code)
                    .method(method)
                    .expiresInMinutes(10)
                    .build();

            eventPublisher.publishTwoFactorCode(event);
            log.info("Published TWO_FACTOR_CODE_SENT event for user: {} via {}", user.getId(), method);

        } catch (Exception e) {
            log.error("Failed to publish TWO_FACTOR_CODE_SENT event for user: {}", user.getId(), e);
        }
    }
    
    /**
     * Publishes EMAIL_VERIFICATION_REQUESTED event when user requests email verification
     */
    public void publishEmailVerificationRequestEvent(User user, String verificationToken, int tokenExpiryHours) {
        try {
            // Determine if user is a customer (for routing to correct portal)
            boolean isCustomer = user.getRoles().stream()
                    .anyMatch(role -> role.isCustomerRole());

            EmailVerificationRequestEvent event = EmailVerificationRequestEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .language(user.getLanguage())
                    .verificationToken(verificationToken)
                    .tokenExpiryHours(tokenExpiryHours)
                    .isCustomer(isCustomer)
                    .build();

            eventPublisher.publishEmailVerificationRequest(event);
            log.info("Published EMAIL_VERIFICATION_REQUESTED event for user: {} (isCustomer: {})", user.getId(), isCustomer);

        } catch (Exception e) {
            log.error("Failed to publish EMAIL_VERIFICATION_REQUESTED event for user: {}", user.getId(), e);
        }
    }
}
