package com.zote.user.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.TokenType;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserToken;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserTokenRepositoryPort;
import com.zote.user.service.infrastructure.outbound.entities.UserEntity;
import com.zote.user.service.infrastructure.outbound.entities.UserTokenEntity;
import com.zote.user.service.infrastructure.outbound.persistence.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserTokenRepositoryPortImpl implements UserTokenRepositoryPort {

    private final UserTokenRepository userTokenRepository;

    @Override
    public UserToken saveToken(UserToken token, User user) {
        log.info("Saving token for user: {}, type: {}", token.getUserId(), token.getTokenType());
        return userTokenRepository.save(UserTokenEntity.toEntity(token, UserEntity.toEntity(user))).toDto();
    }

    @Override
    public Optional<UserToken> findByToken(String token) {
        log.info("Finding token by token string");
        return userTokenRepository.findByToken(token)
                .map(UserTokenEntity::toDto);
    }

    @Override
    public Optional<UserToken> findByTokenAndType(String token, TokenType tokenType) {
        log.info("Finding token by token string and type: {}", tokenType);
        return userTokenRepository.findByTokenAndTokenType(token, tokenType)
                .map(UserTokenEntity::toDto);
    }

    @Override
    public List<UserToken> findActiveTokensByUserAndType(String userId, TokenType tokenType) {
        log.info("Finding active tokens for user: {}, type: {}", userId, tokenType);
        return userTokenRepository.findActiveTokensByUserAndType(userId, tokenType, LocalDateTime.now())
                .stream()
                .map(UserTokenEntity::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markTokenAsUsed(String tokenId) {
        log.info("Marking token as used: {}", tokenId);
        userTokenRepository.findById(tokenId).ifPresent(token -> {
            token.setUsed(true);
            userTokenRepository.save(token);
        });
    }

    @Override
    @Transactional
    public void markAllUserTokensAsUsed(String userId, TokenType tokenType) {
        log.info("Marking all tokens as used for user: {}, type: {}", userId, tokenType);
        userTokenRepository.markAllTokensAsUsed(userId, tokenType);
    }

    @Override
    public void deleteExpiredTokens() {
        log.info("Deleting expired tokens");
        userTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    // Scheduled task to clean up expired tokens (runs daily at 2 AM)
    @Scheduled(cron = "0 0 2 * * ?")
    @org.springframework.transaction.annotation.Transactional
    public void cleanupExpiredTokens() {
        try {
            deleteExpiredTokens();
            log.info("Successfully cleaned up expired tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
            // Don't rethrow - scheduled tasks should not fail silently but also shouldn't crash the app
        }
    }
}

