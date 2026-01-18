package com.zote.resourcebundle.adapter.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "message_template")
public class MessageTemplateEntity extends Auditable {
    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Language language;
    private String subject;
    private String body;

    public static MessageTemplateEntity toEntity(MessageTemplate messageTemplate) {
        MessageTemplateEntity entity = new MessageTemplateEntity();
        BeanUtils.copyProperties(messageTemplate, entity);
        return entity;
    }

    public MessageTemplate toDto() {
        MessageTemplate messageTemplate = new MessageTemplate();
        BeanUtils.copyProperties(this, messageTemplate);
        return messageTemplate;
    }
}
