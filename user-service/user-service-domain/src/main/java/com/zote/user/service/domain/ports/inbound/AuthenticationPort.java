package com.zote.user.service.domain.ports.inbound;

import com.zote.user.service.domain.model.AuthData;
import com.zote.user.service.domain.model.PasswordStrength;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.support.PasswordPolicyService;

public interface AuthenticationPort {

    AuthData authenticate(String username, String password);
    
    AuthData refreshToken(String refreshToken);

    User getCurrentUser();

    void logout();
    
    void requestPasswordReset(String email);
    
    void resetPassword(String token, String newPassword, String twoFactorCode);
    
    void verifyEmail(String token);
    
    void resendVerificationEmail(String email);
    
    void unlockAccount(String token);
    
    PasswordStrength validatePassword(String password);
    
    PasswordPolicyService.PasswordPolicyConfig getPasswordPolicy();
}
