package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.DriverType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyAutoDriver {

    private String id;
    private String policyVersionId;
    private String name;
    private DriverType driverType;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
