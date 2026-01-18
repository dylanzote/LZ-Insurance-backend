package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDistributionResponse {
    
    private String roleName;
    private String roleCode;
    private long userCount;
    private double percentage;
    private long activeCount;
    private long suspendedCount;
}

