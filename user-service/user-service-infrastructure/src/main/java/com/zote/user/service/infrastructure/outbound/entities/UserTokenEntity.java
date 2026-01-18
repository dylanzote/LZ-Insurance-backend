package com.zote.user.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.TokenType;
import com.zote.user.service.domain.model.UserToken;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_tokens", indexes = {
    @Index(name = "idx_user_tokens_token", columnList = "token"),
    @Index(name = "idx_user_tokens_user_type", columnList = "user_id,token_type")
})
public class UserTokenEntity extends Auditable {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Column(nullable = false, unique = true, length = 500)
    private String token;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used;

    public static UserTokenEntity toEntity(UserToken userToken, UserEntity user) {
        UserTokenEntity entity = new UserTokenEntity();
        BeanUtils.copyProperties(userToken, entity);
        entity.setUser(user);
        return entity;
    }

    public UserToken toDto() {
        UserToken userToken = new UserToken();
        BeanUtils.copyProperties(this, userToken);
        userToken.setUserId(this.user != null ? this.user.getId() : null);
        return userToken;
    }
}

