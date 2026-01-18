package com.zote.user.service.api.response;

import lombok.Data;

/**
 * Simple role DTO for responses that don't need full role details
 * For full role information, use RoleResponse
 */
@Data
public class RoleDto {
    private String id;
    private String name;
    
    /**
     * Determines notification preferences
     */
    private boolean isCustomerRole;
}
