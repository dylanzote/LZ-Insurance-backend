package com.zote.notification.service.api.request;

import lombok.Data;

import java.util.Map;

@Data
public class RenderTemplateRequest {
    private Map<String, Object> variables;

    private String locale;
}
