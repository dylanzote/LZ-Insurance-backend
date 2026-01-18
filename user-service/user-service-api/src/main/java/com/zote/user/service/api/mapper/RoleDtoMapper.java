package com.zote.user.service.api.mapper;

import com.zote.user.service.api.response.RoleResponse;
import com.zote.user.service.domain.model.Role;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface RoleDtoMapper {

    RoleDtoMapper INSTANCE = Mappers.getMapper(RoleDtoMapper.class);

    @Mapping(target = "permissions", source = "permissions")
    Role toDto(RoleResponse roleResponse);

    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toEntity(Role role);
}
