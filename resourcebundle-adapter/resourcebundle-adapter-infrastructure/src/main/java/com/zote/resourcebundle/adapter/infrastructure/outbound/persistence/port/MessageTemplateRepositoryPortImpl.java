package com.zote.resourcebundle.adapter.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import com.zote.resourcebundle.adapter.domain.ports.outbound.MessageTemplateRepositoryPort;
import com.zote.resourcebundle.adapter.infrastructure.outbound.entities.MessageTemplateEntity;
import com.zote.resourcebundle.adapter.infrastructure.outbound.persistence.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageTemplateRepositoryPortImpl implements MessageTemplateRepositoryPort {

    private final MessageTemplateRepository messageTemplateRepository;
    @Override
    public MessageTemplate saveMessageTemplate(MessageTemplate messageTemplate) {
        log.info("Saving message template: {}", messageTemplate);
        return messageTemplateRepository.save(MessageTemplateEntity.toEntity(messageTemplate)).toDto();
    }

    @Override
    public void deleteMessageTemplateById(String messageTemplateId) {
        log.info("Deleting message template with id: {}", messageTemplateId);
        messageTemplateRepository.deleteById(messageTemplateId);
    }

    @Override
    public MessageTemplate findMessageTemplateById(String messageTemplateId) {
        log.info("Finding message template with id: {}", messageTemplateId);
        return messageTemplateRepository.findById(messageTemplateId).map(MessageTemplateEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Message template not found with id"));
    }

    @Override
    public MessageTemplate findMessageTemplateByKeyAndLanguage(String name, Language language) {
        log.info("Finding message template with key: {} and language: {}", name, language);
        return messageTemplateRepository.findByNameAndLanguage(name, language).map(MessageTemplateEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Message template not found"));
    }

    @Override
    public Page<MessageTemplate> findTemplateByPage(Pageable pageable) {
        log.info("Finding all message templates with page: {}", pageable);
        return messageTemplateRepository.findAll(pageable).map(MessageTemplateEntity::toDto);
    }

    @Override
    public List<MessageTemplate> getAllMessageTemplates() {
        return messageTemplateRepository.findAll().stream()
                .map(MessageTemplateEntity::toDto)
                .toList();
    }

    @Override
    public boolean messageTemplateById(String messageTemplateId) {
        log.info("Checking if message template exists by id: {}", messageTemplateId);
        return messageTemplateRepository.existsById(messageTemplateId);
    }

    @Override
    public boolean existByNameAndLanguage(String name, Language language) {
        log.info("Verifies if message template exists by name and language: {} and {}", name, language);
        return messageTemplateRepository.existsByNameAndLanguage(name, language);
    }
}
