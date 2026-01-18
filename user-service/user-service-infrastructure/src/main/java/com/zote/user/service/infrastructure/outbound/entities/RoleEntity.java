package com.zote.user.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.user.service.domain.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "roles")
public class RoleEntity extends Auditable {
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_customer_role", nullable = false)
    private boolean isCustomerRole = false;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) // Only persist new entities When you create a user and associate it with multiple roles that share the same permissions, these permissions are represented as multiple instances of PermissionEntity. If you're using CascadeType.ALL, Hibernate tries to persist all those instances as new objects, leading to the conflict since the database already has the permission with that ID
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();

    public static RoleEntity toEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        BeanUtils.copyProperties(role, entity);
        Set<PermissionEntity> permissionEntities = role.getPermissions()
                .stream()
                .map(PermissionEntity::toEntity)
                .collect(Collectors.toSet());
        entity.setPermissions(permissionEntities);
        return entity;
    }

    public Role toDto() {
        Role role = new Role();
        BeanUtils.copyProperties(this, role);
        role.setPermissions(this.getPermissions().stream()
               .map(PermissionEntity::toDto)
               .collect(Collectors.toSet()));
        return role;
    }
}
