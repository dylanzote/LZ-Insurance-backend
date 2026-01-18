package com.zote.user.service.api.request;

import com.zote.user.service.domain.model.RoleRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Set;

public record UpdateRoleRequest(
        String name,
        
        String description,
        
        /**
         * Indicates if this is a customer-facing role
         * Optional - if not provided, existing value is kept
         * Determines notification preferences:
         * - true: Customer role (push notifications + bilingual emails)
         * - false: System role (web notifications + critical emails)
         */
        Boolean isCustomerRole,
        
        Set<String> permissionIds
) {

        public RoleRequest toRolerequest() {
                RoleRequest roleRequest = new RoleRequest();
                BeanUtils.copyProperties(this, roleRequest);
                return roleRequest;
        }

}
