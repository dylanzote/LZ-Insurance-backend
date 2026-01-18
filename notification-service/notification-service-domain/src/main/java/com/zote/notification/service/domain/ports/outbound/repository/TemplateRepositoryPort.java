package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.NotificationTemplate;
import com.zote.notification.service.domain.model.TemplateTranslation;

import java.util.List;
import java.util.Optional;

public interface TemplateRepositoryPort {
    NotificationTemplate save(NotificationTemplate template);
    NotificationTemplate findById(String id);
    Optional<NotificationTemplate> findByName(String name);
    NotificationTemplate findByChannel(NotificationChannel channel);
    List<NotificationTemplate> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive);
    void deleteById(String id);
    List<TemplateTranslation> findTranslationsByTemplateId(String templateId);
    TemplateTranslation findTranslation(String templateId, String locale);

    TemplateTranslation saveTranslation(TemplateTranslation translation);
    void deleteTranslation(String templateId, String locale);
    void deleteAllTranslations(String templateId);
}
