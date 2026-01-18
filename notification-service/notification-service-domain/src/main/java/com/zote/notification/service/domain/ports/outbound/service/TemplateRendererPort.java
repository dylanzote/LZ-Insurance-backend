package com.zote.notification.service.domain.ports.outbound.service;

import java.util.Map;

public interface TemplateRendererPort {
    String renderTemplate(String templateId, Map<String, Object> variables, String locale);
    String renderSubject(String templateId, Map<String, Object> variables, String locale);
    String renderHtml(String templateId, Map<String, Object> variables, String locale);
    void validateVariables(String templateId, Map<String, Object> variables);
}
