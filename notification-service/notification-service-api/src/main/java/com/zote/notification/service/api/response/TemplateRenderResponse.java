package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.TemplateRenderResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class TemplateRenderResponse {
    private String templateId;
    private String locale;
    private String renderedContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static TemplateRenderResponse toTemplateRenderResponse(TemplateRenderResult result) {
        var response = new TemplateRenderResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }
}
