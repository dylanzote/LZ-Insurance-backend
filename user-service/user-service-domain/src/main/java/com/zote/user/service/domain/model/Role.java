package com.zote.user.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Role {
    private String id;
    private String name;
    private String description;
    
    /**
     * Indicates if this is a customer-facing role
     * true = Customer role (receives push notifications + bilingual emails)
     * false = System role (receives web notifications + critical emails only)
     */
    private boolean isCustomerRole;
    
    private Set<Permission> permissions;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
