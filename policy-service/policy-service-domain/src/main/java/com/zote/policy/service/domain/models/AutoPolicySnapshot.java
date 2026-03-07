package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPolicySnapshot implements PolicySnapshot {
    private VehicleDetails vehicle;
    private List<Driver> drivers;

    @Override
    public PolicyType getPolicyType() {
        return PolicyType.AUTO;
    }
}
