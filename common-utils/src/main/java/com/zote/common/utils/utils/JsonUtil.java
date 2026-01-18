package com.zote.common.utils.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zote.common.utils.exceptions.FunctionalError;
import lombok.SneakyThrows;

import java.util.Objects;


public class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule());

    @SneakyThrows
    public static String serialize(Object src) {
        if (Objects.isNull(src))
            return "";
        else
            return src instanceof String ? src.toString() : objectMapper.writeValueAsString(src);
    }

    @SneakyThrows
    public static <T> T deserialize(String src, Class<T> clazz) {
        if (src == null || src.isEmpty()) {
            throw new FunctionalError("JSON string is null or empty");
        }
        return objectMapper.readValue(src, clazz);
    }

    @SneakyThrows
    public static <T> T deserialize(String json, TypeReference<T> typeRef) {
        if (json == null || json.isEmpty()) {
            throw new FunctionalError("JSON string is null or empty");
        }

        return objectMapper.readValue(json, typeRef);
    }
}
