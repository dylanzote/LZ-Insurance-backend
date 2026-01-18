package com.zote.user.service.api.usecase;

import com.zote.user.service.api.controller.PermissionApi;
import com.zote.user.service.api.request.CreatePermissionRequest;
import com.zote.user.service.api.request.UpdatePermissionRequest;
import com.zote.user.service.api.response.PermissionResponse;
import com.zote.user.service.domain.model.Permission;
import com.zote.user.service.domain.ports.inbound.PermissionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionService implements PermissionApi {

    private final PermissionPort permissionPort;

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("incoming request for creating permission {}", request);
        return PermissionResponse.toResponse(permissionPort.createPermission(request.name()));
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        log.info("incoming request for getting all permissions");
        return permissionPort.getAllPermissions().stream().map(PermissionResponse::toResponse).toList();
    }

    @Override
    public PermissionResponse getPermission(String id) {
        log.info("incoming request for getting permission with id {}", id);
        return PermissionResponse.toResponse(permissionPort.findPermissionById(id));
    }

    @Override
    public PermissionResponse updatePermission(UpdatePermissionRequest request) {
        log.info("incoming request for updating permission {}", request);
        return PermissionResponse.toResponse(permissionPort.updatePermission(Permission.builder().id(request.id()).name(request.name()).build()));
    }

    @Override
    public void deletePermission(String id) {
        log.info("incoming request for deleting permission with id {}", id);
        permissionPort.deletePermission(id);
    }
}
