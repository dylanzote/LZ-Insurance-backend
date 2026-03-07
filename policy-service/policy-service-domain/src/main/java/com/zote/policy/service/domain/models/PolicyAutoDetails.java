package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PolicyAutoDetails {
    private String policyVersionId;
    private String make;
    private String model;
    private Integer year;
    private VehicleType vehicleType;
    private String vin;
    private String licensePlate;
    private Map<String, Object> attributes;
    private List<PolicyAutoDriver> drivers;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
