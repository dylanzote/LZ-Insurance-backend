package com.zote.user.service.domain.model;

import com.zote.common.utils.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {
    private String id;
    private String userId;
    private String token;
    private TokenType tokenType;
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}

