package com.zote.user.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDistribution {
    
    private String roleName;
    private String roleCode;
    private long userCount;
    private double percentage;
    private long activeCount;
    private long suspendedCount;
}

