package com.zote.user.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.Permission;
import com.zote.user.service.domain.ports.inbound.PermissionPort;
import com.zote.user.service.domain.ports.outbound.PermissionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionImpl implements PermissionPort {

    private final PermissionRepositoryPort permissionRepositoryPort;
    @Override
    public Permission createPermission(String permissionName) {
        Permission permission = Permission.builder()
                .id(UUID.randomUUID().toString())
                .name(permissionName)
                .build();
        return permissionRepositoryPort.savePermission(permission);
    }

    @Override
    public Permission updatePermission(Permission permission) {
        if (permissionRepositoryPort.existsById(permission.getId())) {
            return permissionRepositoryPort.savePermission(permission);
        }
        else throw new FunctionalError("permission does not exist with id " + permission.getId());
    }

    @Override
    public void deletePermission(String permissionId) {
        permissionRepositoryPort.deletePermissionById(permissionId);
    }

    @Override
    public Permission findPermissionById(String permissionId) {
        return permissionRepositoryPort.findPermissionById(permissionId);
    }

    @Override
    public Permission findPermissionByName(String permissionName) {
        return permissionRepositoryPort.findPermissionByName(permissionName);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepositoryPort.getAllPermissions();
    }
}
