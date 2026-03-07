package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPolicySnapshot implements PolicySnapshot {

    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private List<Traveller> travellers;
    private List<String> destinations;

    @Override
    public PolicyType getPolicyType() {
        return PolicyType.TRAVEL;
    }
}
