package com.zote.user.service.api.request;


import com.zote.user.service.domain.model.RoleRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Set;

public record CreateRoleRequest(
        @NotNull(message = "Role name is required")
        String name,
        
        @NotNull(message = "Role description is required")
        String description,

        Boolean isCustomerRole,
        
        @NotNull(message = "Permission IDs are required")
        Set<String> permissionIds
) {
        public  RoleRequest toRolerequest() {
                RoleRequest roleRequest = new RoleRequest();
                BeanUtils.copyProperties(this, roleRequest);
                // Default to false (system role) if not provided
                roleRequest.setIsCustomerRole(isCustomerRole != null ? isCustomerRole : false);
                return roleRequest;
        }
}
