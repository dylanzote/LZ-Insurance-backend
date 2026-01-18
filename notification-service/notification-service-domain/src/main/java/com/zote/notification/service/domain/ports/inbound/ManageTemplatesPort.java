package com.zote.notification.service.domain.ports.inbound;

import com.zote.notification.service.domain.model.*;

import java.util.Map;

public interface ManageTemplatesPort {
    NotificationTemplate createTemplate(CreateTemplateData data);
    NotificationTemplate updateTemplate(UpdateTemplateData data);
    void deleteTemplate(String templateId);
    NotificationTemplate getTemplate(String templateId);
    TemplatesResult searchTemplates(SearchTemplatesQuery query);
    NotificationTemplate addTranslation(AddTranslationData data);
    NotificationTemplate updateTranslation(UpdateTranslationData data);
    TemplateRenderResult renderTemplate(String templateId, Map<String, Object> variables, String locale);
}
