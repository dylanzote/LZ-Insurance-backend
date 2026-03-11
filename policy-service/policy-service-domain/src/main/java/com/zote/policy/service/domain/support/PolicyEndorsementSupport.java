package com.zote.policy.service.domain.support;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PolicyEndorsementSupport {
    public Map<String, Object> applyChanges(Map<String, Object> currentSnapshot,
                                            Map<String, Object> changes) {
        Map<String, Object> updated = new HashMap<>();
        if (currentSnapshot != null) {
            updated.putAll(currentSnapshot);
        }
        if (changes != null) {
            updated.putAll(changes);
        }
        return updated;
    }
}
