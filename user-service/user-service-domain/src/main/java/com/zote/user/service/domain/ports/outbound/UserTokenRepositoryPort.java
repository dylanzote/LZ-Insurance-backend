package com.zote.user.service.domain.ports.outbound;

import com.zote.common.utils.enums.TokenType;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserToken;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepositoryPort {
    
    UserToken saveToken(UserToken token, User user);
    
    Optional<UserToken> findByToken(String token);
    
    Optional<UserToken> findByTokenAndType(String token, TokenType tokenType);
    
    List<UserToken> findActiveTokensByUserAndType(String userId, TokenType tokenType);
    
    void markTokenAsUsed(String tokenId);
    
    void markAllUserTokensAsUsed(String userId, TokenType tokenType);
    
    void deleteExpiredTokens();
}

