package com.zote.user.service.api.response;

import com.zote.user.service.domain.model.AuthData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthResponse {
    private String accessToken;
    private int expiresIn;
    private int refreshExpiresIn;
    private String refreshToken;
    private UserResponse user;

    public static AuthResponse toAuthResponse(AuthData authData) {
        AuthResponse authResponse = new AuthResponse();
        BeanUtils.copyProperties(authData, authResponse);
        authResponse.setUser(UserResponse.toResponse(authData.getUser()));
        return authResponse;
    }
}
