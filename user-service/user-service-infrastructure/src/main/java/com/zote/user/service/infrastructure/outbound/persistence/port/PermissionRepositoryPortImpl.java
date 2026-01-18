package com.zote.user.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.Permission;
import com.zote.user.service.domain.model.PermissionDto;
import com.zote.user.service.domain.ports.outbound.PermissionRepositoryPort;
import com.zote.user.service.infrastructure.outbound.entities.PermissionEntity;
import com.zote.user.service.infrastructure.outbound.persistence.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionRepositoryPortImpl implements PermissionRepositoryPort {

    private final PermissionRepository permissionRepository;
    @Override
    public Permission savePermission(Permission permission) {
        log.info("saving permission {}", permission);
        return permissionRepository.save(PermissionEntity.toEntity(permission)).toDto();
    }

    @Override
    public void deletePermissionById(String permissionId) {
        log.info("deleting permission with permission id {}", permissionId);
        permissionRepository.deleteById(permissionId);
    }

    @Override
    public Permission findPermissionById(String permissionId) {
        log.info("getting permission with permission id {}", permissionId);
        return permissionRepository.findById(permissionId).map(PermissionEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find permission with permission id " + permissionId));
    }

    @Override
    public Permission findPermissionByName(String permissionName) {
        log.info("getting permission with permission name {}", permissionName);
        return permissionRepository.findByName(permissionName).map(PermissionEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find permission with permission name " + permissionName));
    }

    @Override
    public List<Permission> getAllPermissions() {
        log.info("getting all permissions");
        return permissionRepository.findAll().stream().map(PermissionEntity::toDto).toList();
    }

    @Override
    public boolean existsById(String permissionId) {
        log.info("verifies if permission exists by id {}", permissionId);
        return permissionRepository.existsById(permissionId);
    }
}
