package com.zote.user.service.domain.ports.inbound;

import com.zote.user.service.domain.model.Role;
import com.zote.user.service.domain.model.RoleDto;
import com.zote.user.service.domain.model.RoleRequest;

import java.util.List;

public interface RolePort {

    Role createRole(RoleRequest roleRequest);

    Role updateRole(RoleRequest roleRequest);

    void deleteRole(String roleId);

    Role findRoleById(String roleId);

    Role findRoleByName(String roleName);

    List<Role> getAllRoles();
}
