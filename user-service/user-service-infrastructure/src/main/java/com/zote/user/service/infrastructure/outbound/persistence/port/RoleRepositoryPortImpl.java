package com.zote.user.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.Role;
import com.zote.user.service.domain.ports.outbound.RoleRepositoryPort;
import com.zote.user.service.infrastructure.outbound.entities.RoleEntity;
import com.zote.user.service.infrastructure.outbound.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoleRepositoryPortImpl implements RoleRepositoryPort {

    private final RoleRepository roleRepository;
    @Override
    public Role saveRole(Role role) {
        log.info("Saving role {}", role);
        return roleRepository.save(RoleEntity.toEntity(role)).toDto();
    }

    @Override
    public void deleteRoleById(String roleId) {
        log.info("Deleting role with roleId {}", roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findRoleById(String roleId) {
        log.info("Getting role with roleId {}", roleId);
        return roleRepository.findById(roleId).map(RoleEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find role with roleId " + roleId));
    }

    @Override
    public Role findRoleByName(String roleName) {
        log.info("Getting role with roleName {}", roleName);
        return roleRepository.findByName(roleName).map(RoleEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find role with roleName " + roleName));
    }

    @Override
    public List<Role> getAllRoles() {
        log.info("Getting all roles");
        return roleRepository.findAll().stream().map(RoleEntity::toDto).toList();
    }

    @Override
    public boolean existsById(String roleId) {
        log.info("Verifies if role exists by id {}", roleId);
        return roleRepository.existsById(roleId);
    }

    @Override
    public boolean existsByName(String roleName) {
        log.info("Verifies if role exists by name {}", roleName);
        return roleRepository.existsByName(roleName);
    }
}
