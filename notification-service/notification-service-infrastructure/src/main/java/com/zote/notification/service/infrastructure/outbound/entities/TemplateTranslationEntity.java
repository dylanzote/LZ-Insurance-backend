package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.notification.service.domain.model.TemplateTranslation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "template_translations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateTranslationEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "template_id", nullable = false)
    private String templateId;

    @Column(nullable = false)
    private String locale;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;


    public static TemplateTranslationEntity toEntity(TemplateTranslation templateTranslation) {
        TemplateTranslationEntity entity = new TemplateTranslationEntity();
        BeanUtils.copyProperties(templateTranslation, entity);
        return entity;
    }

    public TemplateTranslation toDto() {
        TemplateTranslation templateTranslation = new TemplateTranslation();
        BeanUtils.copyProperties(this, templateTranslation);
        return templateTranslation;
    }
}
