package com.zote.user.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.TokenType;
import com.zote.user.service.infrastructure.outbound.entities.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, String> {
    
    Optional<UserTokenEntity> findByTokenAndTokenType(String token, TokenType tokenType);
    
    Optional<UserTokenEntity> findByToken(String token);
    
    List<UserTokenEntity> findByUser_IdAndTokenType(String userId, TokenType tokenType);
    
    @Modifying
    @Query("UPDATE UserTokenEntity t SET t.used = true WHERE t.user.id = :userId AND t.tokenType = :tokenType")
    void markAllTokensAsUsed(@Param("userId") String userId, @Param("tokenType") TokenType tokenType);
    
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserTokenEntity t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM UserTokenEntity t WHERE t.user.id = :userId AND t.tokenType = :tokenType AND t.used = false AND t.expiresAt > :now ORDER BY t.createdAt DESC")
    List<UserTokenEntity> findActiveTokensByUserAndType(@Param("userId") String userId, 
                                                         @Param("tokenType") TokenType tokenType,
                                                         @Param("now") LocalDateTime now);
}

