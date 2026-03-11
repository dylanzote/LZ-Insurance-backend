package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.ComplianceViolation;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.ports.outbound.ComplianceViolationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Detects and stores compliance violations (14.3).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceSupport {

    public static final String TYPE_PREMIUM_EXCEEDS_MAX = "PREMIUM_EXCEEDS_MAX";
    public static final String TYPE_PREMIUM_BELOW_MIN = "PREMIUM_BELOW_MIN";
    public static final String TYPE_GRACE_PERIOD_EXCEEDED = "GRACE_PERIOD_EXCEEDED";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_MEDIUM = "MEDIUM";
    public static final String SEVERITY_LOW = "LOW";

    private final ComplianceViolationPort complianceViolationPort;
    private final MessagingSupport messagingSupport;

    /**
     * Check policy against product config and store violation if constraints breached.
     * Does not throw - only records for review.
     */
    public void checkAndRecordViolations(Policy policy, PolicyProductConfig config) {
        if (policy.getPremiumTotal() != null && config.getMinPremium() != null
                && policy.getPremiumTotal().compareTo(config.getMinPremium()) < 0) {
            recordViolation(policy.getId(), TYPE_PREMIUM_BELOW_MIN,
                    "Premium " + policy.getPremiumTotal() + " is below product minimum " + config.getMinPremium(),
                    SEVERITY_MEDIUM, Map.of("premium", policy.getPremiumTotal().toString(), "minAllowed", config.getMinPremium().toString()));
        }
        if (policy.getPremiumTotal() != null && config.getMaxPremium() != null
                && policy.getPremiumTotal().compareTo(config.getMaxPremium()) > 0) {
            recordViolation(policy.getId(), TYPE_PREMIUM_EXCEEDS_MAX,
                    "Premium " + policy.getPremiumTotal() + " exceeds product maximum " + config.getMaxPremium(),
                    SEVERITY_HIGH, Map.of("premium", policy.getPremiumTotal().toString(), "maxAllowed", config.getMaxPremium().toString()));
        }
        if (policy.getGracePeriodDays() != null && config.getMaxGracePeriodDays() != null
                && policy.getGracePeriodDays() > config.getMaxGracePeriodDays()) {
            recordViolation(policy.getId(), TYPE_GRACE_PERIOD_EXCEEDED,
                    "Grace period " + policy.getGracePeriodDays() + " days exceeds product maximum " + config.getMaxGracePeriodDays(),
                    SEVERITY_MEDIUM, Map.of("gracePeriod", policy.getGracePeriodDays(), "maxAllowed", config.getMaxGracePeriodDays()));
        }
    }

    public ComplianceViolation recordViolation(String policyId, String violationType, String description,
                                               String severity, Map<String, Object> metadata) {
        var violation = ComplianceViolation.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .violationType(violationType)
                .description(description)
                .severity(severity)
                .status("OPEN")
                .detectedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();
        violation = complianceViolationPort.save(violation);
        messagingSupport.publishComplianceViolationDetectedEvent(violation);
        return violation;
    }
}
