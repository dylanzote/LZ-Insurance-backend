package com.zote.policy.service.domain.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class SnapshotMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static Map<String, Object> toMap(Object snapshot) {
        return MAPPER.convertValue(snapshot, new TypeReference<>() {});
    }
}
