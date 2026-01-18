package com.zote.user.service.api.response;

import com.zote.user.service.domain.model.Permission;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private String id;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static PermissionResponse toResponse(Permission permission) {
        PermissionResponse permissionResponse = new PermissionResponse();
        BeanUtils.copyProperties(permission, permissionResponse);
        return permissionResponse;
    }
}
