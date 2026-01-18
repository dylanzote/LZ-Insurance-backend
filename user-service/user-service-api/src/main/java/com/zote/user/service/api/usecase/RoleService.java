package com.zote.user.service.api.usecase;

import com.zote.user.service.api.controller.RoleApi;
import com.zote.user.service.api.request.CreateRoleRequest;
import com.zote.user.service.api.request.UpdateRoleRequest;
import com.zote.user.service.api.response.RoleResponse;
import com.zote.user.service.domain.ports.inbound.RolePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoleService implements RoleApi {

    private final RolePort rolePort;

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("incoming request for creating role {}", request);
        return RoleResponse.toResponse(rolePort.createRole(request.toRolerequest()));
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        log.info("incoming request for getting all roles");
        return rolePort.getAllRoles().stream().map(RoleResponse::toResponse).toList();
    }

    @Override
    public RoleResponse getRole(String id) {
        log.info("incoming request for getting role with id {}", id);
        return RoleResponse.toResponse(rolePort.findRoleById(id));
    }

    @Override
    public RoleResponse updateRole(UpdateRoleRequest request) {
        log.info("incoming request for updating role {}", request);
        return RoleResponse.toResponse(rolePort.updateRole(request.toRolerequest()));
    }

    @Override
    public void deleteRole(String id) {
        log.info("incoming request for deleting role with id {}", id);
        rolePort.deleteRole(id);
    }
}
