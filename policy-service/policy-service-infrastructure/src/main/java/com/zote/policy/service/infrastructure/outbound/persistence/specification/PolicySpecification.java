package com.zote.policy.service.infrastructure.outbound.persistence.specification;

import com.zote.policy.service.domain.models.PolicySearchCriteria;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic policy search and filtering (12.1, 12.2).
 */
public final class PolicySpecification {

    private PolicySpecification() {
    }

    public static Specification<PolicyEntity> withCriteria(PolicySearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), criteria.getCustomerId()));
            }
            if (criteria.getAgentId() != null) {
                predicates.add(cb.equal(root.get("agentId"), criteria.getAgentId()));
            }
            if (criteria.getBranchId() != null) {
                predicates.add(cb.equal(root.get("branchId"), criteria.getBranchId()));
            }
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }
            if (criteria.getType() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.getType()));
            }
            if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
                String pattern = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("policyNumber")), pattern),
                        cb.like(cb.lower(root.get("productId")), pattern)
                ));
            }
            if (criteria.getActiveOnDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("effectiveDate"), criteria.getActiveOnDate()));
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), criteria.getActiveOnDate()));
            }
            if (criteria.getExpiringFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), criteria.getExpiringFrom()));
            }
            if (criteria.getExpiringTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expiryDate"), criteria.getExpiringTo()));
            }
            if (criteria.getPremiumMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("premiumTotal"), criteria.getPremiumMin()));
            }
            if (criteria.getPremiumMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("premiumTotal"), criteria.getPremiumMax()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
