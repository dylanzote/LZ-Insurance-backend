package com.zote.notification.service.infrastructure.adapters.bridge.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
/**
 * Role information from user-service
 * Used to determine notification preferences
 */
@Data
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    
    /**
     * Determines notification preferences:
     * - true: Customer role (push notifications + bilingual emails)
     * - false: System role (web notifications + critical emails)
     */
    private boolean isCustomerRole;
    
    private Set<PermissionResponse> permissions;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
