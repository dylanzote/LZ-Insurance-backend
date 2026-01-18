package com.zote.user.service.domain.ports.inbound;

import com.zote.user.service.domain.model.Permission;
import com.zote.user.service.domain.model.PermissionDto;

import java.util.List;


public interface PermissionPort {

    Permission createPermission(String permissionName);

    Permission updatePermission(Permission permission);

    void deletePermission(String permissionId);

    Permission findPermissionById(String permissionId);

    Permission findPermissionByName(String permissionName);

    List<Permission> getAllPermissions();
}
