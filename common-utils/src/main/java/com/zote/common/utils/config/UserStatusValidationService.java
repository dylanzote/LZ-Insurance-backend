package com.zote.common.utils.config;

import com.zote.common.utils.enums.Status;

/**
 * Service interface for validating user status.
 * Services implementing this interface can provide user status validation logic.
 */
public interface UserStatusValidationService {
    
    /**
     * Gets the status of a user by their Keycloak user ID.
     * 
     * @param keycloakUserId The Keycloak user ID
     * @return The user's status, or null if user not found
     */
    Status getUserStatus(String keycloakUserId);
    
    /**
     * Checks if the service is available (i.e., the implementation is present).
     * 
     * @return true if the service is available, false otherwise
     */
    default boolean isAvailable() {
        return true;
    }
}
