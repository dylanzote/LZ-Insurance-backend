package com.zote.user.service.infrastructure.config;

import com.zote.common.utils.config.UserStatusValidationService;
import com.zote.common.utils.enums.Status;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserStatusValidationService for user-service.
 * Provides user status validation functionality to the common filter.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatusValidationServiceImpl implements UserStatusValidationService {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Status getUserStatus(String keycloakUserId) {
        try {
            User user = userRepositoryPort.findUserByKeyCloakId(keycloakUserId);
            return user != null ? user.getStatus() : null;
        } catch (Exception e) {
            log.error("Error getting user status for Keycloak user ID {}: {}", keycloakUserId, e.getMessage());
            return null;
        }
    }
}
