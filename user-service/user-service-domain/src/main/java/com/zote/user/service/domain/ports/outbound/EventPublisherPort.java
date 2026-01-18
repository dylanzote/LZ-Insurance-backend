package com.zote.user.service.domain.ports.outbound;

import com.zote.kafka.adapter.event.*;

public interface EventPublisherPort {

    void publishUserCreated(UserCreatedEvent event);

    void publishUserUpdated(UserUpdatedEvent event);

    void publishUserActivated(UserActivatedEvent event);

    void publishUserSuspended(UserSuspendedEvent event);

    void publishPasswordChanged(PasswordChangedEvent event);
    
    void publishPasswordResetRequest(PasswordResetRequestEvent event);
    
    void publishEmailVerificationRequest(EmailVerificationRequestEvent event);
    
    void publishFailedLogin(FailedLoginEvent event);
    
    void publishAccountLocked(AccountLockedEvent event);
    
    void publishTwoFactorCode(TwoFactorCodeEvent event);
}

