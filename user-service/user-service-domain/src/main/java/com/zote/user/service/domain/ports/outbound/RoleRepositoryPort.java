package com.zote.user.service.domain.ports.outbound;

import com.zote.user.service.domain.model.Role;
import com.zote.user.service.domain.model.RoleDto;

import java.util.List;

public interface RoleRepositoryPort {

    Role saveRole(Role role);

    void deleteRoleById(String roleId);

    Role findRoleById(String roleId);

    Role findRoleByName(String roleName);

    List<Role> getAllRoles();

    boolean existsById(String roleId);

    boolean existsByName(String roleName);
}
