package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.notification.service.domain.model.UserInfo;

import java.util.Optional;

/**
 * Port for interacting with User Service
 * Used to fetch user information like email, preferences, etc.
 */
public interface UserServicePort {
    Optional<UserInfo> getUserById(String userId);

}

