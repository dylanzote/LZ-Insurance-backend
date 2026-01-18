package com.zote.user.service.domain.ports.outbound;

import com.zote.user.service.domain.model.Permission;
import com.zote.user.service.domain.model.PermissionDto;

import java.util.List;

public interface PermissionRepositoryPort {

    Permission savePermission(Permission permission);

    void deletePermissionById(String permissionId);

    Permission findPermissionById(String permissionId);

    Permission findPermissionByName(String permissionName);

    List<Permission> getAllPermissions();

    boolean existsById(String permissionId);
}
