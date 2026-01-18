package com.zote.user.service.api.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkUserOperationRequest {
    
    private List<String> userIds;
    private String operation; // ACTIVATE, SUSPEND, DELETE, ASSIGN_ROLE, REMOVE_ROLE
    private String roleId; // For ASSIGN_ROLE and REMOVE_ROLE operations
}

