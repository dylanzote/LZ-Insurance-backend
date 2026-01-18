package com.zote.user.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.user.service.domain.model.Permission;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.beans.BeanUtils;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "permissions")
public class PermissionEntity extends Auditable {
    @Id
    private String id;
    private String name;

    public static PermissionEntity toEntity(Permission permission) {
        PermissionEntity entity = new PermissionEntity();
        BeanUtils.copyProperties(permission, entity);
        return entity;
    }

    public Permission toDto() {
        Permission permission = new Permission();
        BeanUtils.copyProperties(this, permission);
        return permission;
    }
}
