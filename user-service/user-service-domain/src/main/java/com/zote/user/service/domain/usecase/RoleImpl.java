package com.zote.user.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.keycloak.adapter.KeyCloakService;
import com.zote.user.service.domain.model.*;
import com.zote.user.service.domain.ports.inbound.RolePort;
import com.zote.user.service.domain.ports.outbound.PermissionRepositoryPort;
import com.zote.user.service.domain.ports.outbound.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleImpl implements RolePort {

    private final RoleRepositoryPort roleRepositoryPort;

    private final PermissionRepositoryPort permissionRepositoryPort;

    private final KeyCloakService keyCloakService;
    @Override
    public Role createRole(RoleRequest roleRequest) {
        if (!roleRepositoryPort.existsByName(roleRequest.getName())) {
            var permissions  = this.getPermissions(roleRequest);
            var role = Role.builder()
                    .id(UUID.randomUUID().toString())
                    .name(roleRequest.getName())
                    .description(roleRequest.getDescription())
                    .permissions(permissions).build();
            keyCloakService.addRealmRole(role.getName(), role.getDescription());
            return roleRepositoryPort.saveRole(role);
        }
        throw new FunctionalError("role already exists with name " + roleRequest.getName());
    }

    @Override
    public Role updateRole(RoleRequest roleRequest) {
        var role = roleRepositoryPort.findRoleByName(roleRequest.getName());
        var permissions = this.getPermissions(roleRequest);
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setPermissions(permissions);
        keyCloakService.addRealmRole(role.getName(), role.getDescription());
        return roleRepositoryPort.saveRole(role);
    }

    @Override
    public void deleteRole(String roleId) {
        var role = roleRepositoryPort.findRoleById(roleId);
        keyCloakService.deleteRealmRole(role.getName());
        roleRepositoryPort.deleteRoleById(roleId);
    }

    @Override
    public Role findRoleById(String roleId) {
        return roleRepositoryPort.findRoleById(roleId);
    }

    @Override
    public Role findRoleByName(String roleName) {
        return roleRepositoryPort.findRoleByName(roleName);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepositoryPort.getAllRoles();
    }

    private Set<Permission> getPermissions(RoleRequest roleRequest) {
        return roleRequest.getPermissionIds().stream()
                .map(permissionRepositoryPort::findPermissionById)
                .collect(Collectors.toSet());
    }
}
