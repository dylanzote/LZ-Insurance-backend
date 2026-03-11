package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class RequiredDocumentResponse {

    private String id;
    private String productConfigId;
    private RequiredDocumentType documentType;
    private boolean mandatory;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static RequiredDocumentResponse from(PolicyRequiredDocument document) {
        RequiredDocumentResponse response = new RequiredDocumentResponse();
        BeanUtils.copyProperties(document, response);
        return response;
    }
}
