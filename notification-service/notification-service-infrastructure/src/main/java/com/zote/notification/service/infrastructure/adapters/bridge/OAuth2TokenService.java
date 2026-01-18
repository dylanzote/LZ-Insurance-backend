package com.zote.notification.service.infrastructure.adapters.bridge;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.request.HttpService;
import com.zote.keycloak.adapter.KeyCloakService;
import com.zote.keycloak.adapter.model.KeycloakProperties;
import com.zote.keycloak.adapter.model.TokenData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service for obtaining OAuth2 access tokens using client credentials flow
 * Used for service-to-service authentication with Keycloak
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2TokenService {

    private final HttpService httpService;

    private final KeycloakProperties keycloakProperties;

    private final KeyCloakService keyCloakService;

    private String cachedToken;
    private LocalDateTime tokenExpiry;
    private final ReentrantLock tokenLock = new ReentrantLock();

    public String getAccessToken() {
        tokenLock.lock();
        try {
            if (cachedToken != null && tokenExpiry != null && tokenExpiry.isAfter(LocalDateTime.now())) {
                log.info("Using cached OAuth2 token");
                return cachedToken;
            }

            log.info("Requesting new OAuth2 token from Keycloak");
            var requestData = keyCloakService.buildAuthRequest();

            try {
                TokenData response = httpService.post(keycloakProperties.getAuthServerUrl(), requestData, TokenData.class);
                if (response != null && response.getAccessToken() != null) {
                    cachedToken = response.getAccessToken();
                    // Set expiry slightly before actual expiry to be safe
                    tokenExpiry = LocalDateTime.now().plusSeconds(response.getExpiresIn() - 60); // 60 seconds buffer
                    log.debug("Successfully obtained OAuth2 token, expires in {} seconds", response.getExpiresIn());
                    return cachedToken;
                }
                throw new FunctionalError("Failed to obtain OAuth2 token: invalid response");

            } catch (Exception e) {
                log.error("Failed to obtain OAuth2 token from Keycloak: {}", e.getMessage(), e);
                throw new FunctionalError("Failed to obtain OAuth2 token");
            }
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * Invalidate cached token (useful for testing or forced refresh)
     */
    public void invalidateToken() {
        tokenLock.lock();
        try {
            cachedToken = null;
            tokenExpiry = null;
            log.debug("OAuth2 token cache invalidated");
        } finally {
            tokenLock.unlock();
        }
    }

}

