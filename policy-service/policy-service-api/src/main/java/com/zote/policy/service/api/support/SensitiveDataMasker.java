package com.zote.policy.service.api.support;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Masks sensitive data in audit logs (14.5).
 */
public final class SensitiveDataMasker {

    private static final String MASK = "****";
    private static final int VISIBLE_SUFFIX_LEN = 4;

    private SensitiveDataMasker() {
    }

    /** Mask customer ID - show last 4 chars only. */
    public static String maskCustomerId(String customerId) {
        if (customerId == null || customerId.length() <= VISIBLE_SUFFIX_LEN) {
            return MASK;
        }
        return MASK + customerId.substring(customerId.length() - VISIBLE_SUFFIX_LEN);
    }

    /** Mask policy number - show first 3 and last 4. */
    public static String maskPolicyNumber(String policyNumber) {
        if (policyNumber == null || policyNumber.length() <= 7) {
            return MASK;
        }
        return policyNumber.substring(0, 3) + MASK + policyNumber.substring(policyNumber.length() - 4);
    }

    /** Recursively mask sensitive keys in a map (customerId, policyId, etc.). */
    public static Map<String, Object> maskSensitiveInMap(Map<String, Object> map) {
        if (map == null) return null;
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            String key = e.getKey().toLowerCase();
                            Object val = e.getValue();
                            if (val instanceof String s) {
                                if (key.contains("customer")) return maskCustomerId(s);
                                if (key.contains("policynumber") || key.contains("policy_number")) return maskPolicyNumber(s);
                            }
                            return val;
                        }
                ));
    }
}
