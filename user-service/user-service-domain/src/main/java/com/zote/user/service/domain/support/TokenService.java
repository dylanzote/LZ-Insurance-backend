package com.zote.user.service.domain.support;

import com.zote.common.utils.enums.TokenType;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserToken;
import com.zote.user.service.domain.ports.outbound.UserTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final UserTokenRepositoryPort userTokenRepositoryPort;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 64;
    
    // Token expiration times (in hours)
    private static final int PASSWORD_RESET_EXPIRY_HOURS = 24;
    private static final int EMAIL_VERIFICATION_EXPIRY_HOURS = 72;
    private static final int TWO_FACTOR_EXPIRY_MINUTES = 10;
    private static final int IMPERSONATION_EXPIRY_HOURS = 8;

    public UserToken generatePasswordResetToken(User user) {
        log.info("Generating password reset token for user: {}", user.getId());
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRY_HOURS);

        userTokenRepositoryPort.markAllUserTokensAsUsed(user.getId(), TokenType.PASSWORD_RESET);
        
        UserToken userToken = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .token(token)
                .tokenType(TokenType.PASSWORD_RESET)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        
        return userTokenRepositoryPort.saveToken(userToken, user);
    }

    public UserToken generateEmailVerificationToken(User user) {
        log.info("Generating email verification token for user: {}", user.getId());
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(EMAIL_VERIFICATION_EXPIRY_HOURS);
        
        UserToken userToken = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .token(token)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        
        return userTokenRepositoryPort.saveToken(userToken, user);
    }

    public String generateTwoFactorCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    public UserToken generateTwoFactorToken(User user, String code) {
        log.info("Generating two-factor verification token for user: {}", user.getId());
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TWO_FACTOR_EXPIRY_MINUTES);
        
        // Mark any existing 2FA tokens as used
        userTokenRepositoryPort.markAllUserTokensAsUsed(user.getId(), TokenType.TWO_FACTOR_VERIFICATION);
        
        UserToken userToken = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .token(code)
                .tokenType(TokenType.TWO_FACTOR_VERIFICATION)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        
        return userTokenRepositoryPort.saveToken(userToken, user);
    }

    public UserToken generateImpersonationToken(User user, String adminUserId) {
        log.info("Generating impersonation token for user: {} by admin: {}", user.getId(), adminUserId);
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(IMPERSONATION_EXPIRY_HOURS);
        
        UserToken userToken = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .token(token)
                .tokenType(TokenType.IMPERSONATION)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        
        return userTokenRepositoryPort.saveToken(userToken, user);
    }

    public boolean validateToken(String token, TokenType tokenType) {
        log.info("Validating token of type: {}", tokenType);
        return userTokenRepositoryPort.findByTokenAndType(token, tokenType)
                .map(userToken -> {
                    if (userToken.isValid()) {
                        return true;
                    } else {
                        log.warn("Token is invalid: used={}, expired={}", userToken.isUsed(), userToken.isExpired());
                        return false;
                    }
                })
                .orElse(false);
    }

    public UserToken getToken(String token, TokenType tokenType) {
        return userTokenRepositoryPort.findByTokenAndType(token, tokenType)
                .orElseThrow(() -> new RuntimeException("Token not found or invalid"));
    }

    public void markTokenAsUsed(String token) {
        log.info("Marking token as used");
        userTokenRepositoryPort.findByToken(token)
                .ifPresent(userToken -> userTokenRepositoryPort.markTokenAsUsed(userToken.getId()));
    }
    
    public List<UserToken> findActiveTokensByUserAndType(String userId, TokenType tokenType) {
        return userTokenRepositoryPort.findActiveTokensByUserAndType(userId, tokenType);
    }
    
    public void markAllUserTokensAsUsed(String userId, TokenType tokenType) {
        userTokenRepositoryPort.markAllUserTokensAsUsed(userId, tokenType);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

