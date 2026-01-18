package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.exceptions.UserNotFoundException;
import com.zote.notification.service.domain.model.UserInfo;
import com.zote.notification.service.domain.ports.outbound.service.UserServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for validating users and enriching notification data with user information
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {

    private final UserServicePort userServicePort;

    /**
     * Validate that user exists and return user info
     * @param userId The user ID to validate
     * @return UserInfo if user exists
     * @throws UserNotFoundException if user doesn't exist
     */
    public UserInfo validateAndGetUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        Optional<UserInfo> userInfo = userServicePort.getUserById(userId);
        
        if (userInfo.isEmpty()) {
            log.warn("User not found: {}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        log.debug("User validated successfully: {}", userId);
        return userInfo.get();
    }

    /**
     * Validate that user exists (lightweight check)
     * @param userId The user ID to validate
     * @throws UserNotFoundException if user doesn't exist
     */
    public String getUserLocale(String userId, String defaultLocale) {
        Optional<UserInfo> userInfo = userServicePort.getUserById(userId);
        return userInfo.map(UserInfo::getLocale)
                .filter(locale -> locale != null && !locale.isEmpty())
                .orElse(defaultLocale);
    }
}

