package com.zote.notification.service.infrastructure.adapters.bridge;

import com.zote.common.utils.request.HttpService;
import com.zote.notification.service.domain.model.UserInfo;
import com.zote.notification.service.domain.ports.outbound.service.UserServicePort;
import com.zote.notification.service.infrastructure.adapters.bridge.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Adapter for User Service integration
 * Uses HttpService to communicate with user-service
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

    private final HttpService httpService;

    private final OAuth2TokenService oAuth2TokenService;

    @Value("${notification.external-services.user-service.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    @Value("${notification.external-services.user-service.get-user-url:/user/get/}")
    private String getUserByIdEndpoint;


    @Override
    public Optional<UserInfo> getUserById(String userId) {
        var url = userServiceBaseUrl.concat(getUserByIdEndpoint).concat(userId);
        var token = oAuth2TokenService.getAccessToken();
        return Optional.of(getUserById(url, token).toUserInfo());
    }


    public UserResponseDto getUserById(String url, String token) {
        log.info("getting user by id");
        return httpService.get(url, UserResponseDto.class, token);
    }
}

