package com.zote.notification.service.api.request;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateProviderConfigRequest {
    private Map<String, Object> config;
}
