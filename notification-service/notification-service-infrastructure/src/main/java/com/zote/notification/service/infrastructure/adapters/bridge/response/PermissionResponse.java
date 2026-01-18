package com.zote.notification.service.infrastructure.adapters.bridge.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private String id;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;


}
