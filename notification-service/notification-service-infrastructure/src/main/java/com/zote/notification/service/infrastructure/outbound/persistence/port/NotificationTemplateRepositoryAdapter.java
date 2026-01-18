package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.NotificationTemplate;
import com.zote.notification.service.domain.model.TemplateTranslation;
import com.zote.notification.service.domain.ports.outbound.repository.TemplateRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationTemplateEntity;
import com.zote.notification.service.infrastructure.outbound.entities.TemplateTranslationEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.NotificationTemplateRepository;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.TemplateTranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateRepositoryAdapter implements TemplateRepositoryPort {

    private final NotificationTemplateRepository templateRepository;
    private final TemplateTranslationRepository translationRepository;

    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        log.info("saving notification template: {}", template);
        return templateRepository.save(NotificationTemplateEntity.toEntity(template)).toDto();
    }

    @Override
    public NotificationTemplate findById(String id) {
        log.info("finding notification template with id: {}", id);
        return templateRepository.findById(id)
                .map(NotificationTemplateEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Notification template not found with id: " + id));
    }

    @Override
    public Optional<NotificationTemplate> findByName(String name) {
        log.info("finding notification template with name: {}", name);
        return templateRepository.findByName(name)
                .map(NotificationTemplateEntity::toDto);
    }

    @Override
    public NotificationTemplate findByChannel(NotificationChannel channel) {
        log.info("finding notification template with channel: {}", channel);
        return templateRepository.findByChannel(channel)
                .map(NotificationTemplateEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Notification template not found with channel: " + channel));
    }

    @Override
    public List<NotificationTemplate> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive) {
        log.info("finding notification templates with channel: {} and isActive: {}", channel, isActive);
        return templateRepository.findByChannelAndIsActive(channel, isActive).stream()
                .map(NotificationTemplateEntity::toDto)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        log.info("deleting notification template with id: {}", id);
        templateRepository.deleteById(id);
    }

    @Override
    public List<TemplateTranslation> findTranslationsByTemplateId(String templateId) {
        log.info("finding translations for template id: {}", templateId);
        return translationRepository.findByTemplateId(templateId).stream()
                .map(TemplateTranslationEntity::toDto)
                .toList();
    }

    @Override
    public TemplateTranslation findTranslation(String templateId, String locale) {
        log.info("finding translation for template id: {} and locale: {}", templateId, locale);
        return translationRepository.findByTemplateIdAndLocale(templateId, locale)
                .map(TemplateTranslationEntity::toDto)
                .orElseThrow(() -> new FunctionalError(
                        "Translation not found for template: " + templateId + " and locale: " + locale));
    }

    @Override
    public TemplateTranslation saveTranslation(TemplateTranslation translation) {
        log.info("saving template translation: {}", translation);
        return translationRepository.save(TemplateTranslationEntity.toEntity(translation)).toDto();
    }

    @Override
    public void deleteTranslation(String templateId, String locale) {
        log.info("deleting translation for template id: {} and locale: {}", templateId, locale);
        translationRepository.deleteByTemplateIdAndLocale(templateId, locale);
    }

    @Override
    public void deleteAllTranslations(String templateId) {
        log.info("deleting all translations for template id: {}", templateId);
        translationRepository.deleteByTemplateId(templateId);
    }
}
