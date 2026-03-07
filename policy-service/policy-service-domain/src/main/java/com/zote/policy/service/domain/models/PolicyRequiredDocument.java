package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequiredDocument {
    private String id;
    private String productConfigId;
    private RequiredDocumentType documentType;
    private boolean mandatory;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
