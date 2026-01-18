package com.zote.resourcebundle.adapter.api.response;

import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TemplateResponse {
    private String id;
    private String name;
    private Language language;
    private String subject;
    private String body;

    public static TemplateResponse toResponse(MessageTemplate messageTemplate) {
        TemplateResponse templateResponse = new TemplateResponse();
        BeanUtils.copyProperties(messageTemplate, templateResponse);
        return templateResponse;
    }
}
