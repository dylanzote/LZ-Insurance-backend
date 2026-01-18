package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.notification.service.infrastructure.outbound.entities.TemplateTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateTranslationRepository extends JpaRepository<TemplateTranslationEntity, String> {

    Optional<TemplateTranslationEntity> findByTemplateIdAndLocale(String templateId, String locale);

    List<TemplateTranslationEntity> findByTemplateId(String templateId);

    void deleteByTemplateIdAndLocale(String templateId, String locale);

    void deleteByTemplateId(String templateId);
}
