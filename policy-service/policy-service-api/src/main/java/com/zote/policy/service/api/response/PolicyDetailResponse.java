package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.Policy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Full policy details for back-office (12.3): policy, versions, documents, audit trail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDetailResponse {

    private PolicyResponse policy;
    private List<PolicyVersionResponse> versions;
    private List<PolicyDocumentResponse> documents;
    private List<PolicyStatusHistoryResponse> statusHistory;

    public static PolicyDetailResponse fromPolicy(Policy policy) {
        return PolicyDetailResponse.builder()
                .policy(PolicyResponse.fromPolicy(policy))
                .versions(policy.getVersions() != null
                        ? policy.getVersions().stream().map(PolicyVersionResponse::from).collect(Collectors.toList())
                        : Collections.emptyList())
                .documents(policy.getDocuments() != null
                        ? policy.getDocuments().stream().map(PolicyDocumentResponse::from).collect(Collectors.toList())
                        : Collections.emptyList())
                .statusHistory(policy.getStatusHistory() != null
                        ? policy.getStatusHistory().stream().map(PolicyStatusHistoryResponse::from).collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }
}
