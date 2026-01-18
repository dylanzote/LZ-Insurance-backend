package com.zote.resourcebundle.adapter.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.infrastructure.outbound.entities.MessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplateEntity, String> {

    Optional<MessageTemplateEntity> findByNameAndLanguage(String name, Language language);

    boolean existsByNameAndLanguage(String name, Language language);
}
