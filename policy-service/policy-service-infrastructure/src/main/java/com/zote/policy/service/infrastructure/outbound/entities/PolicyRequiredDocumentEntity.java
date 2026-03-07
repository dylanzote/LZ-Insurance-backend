package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "policy_product_required_document")
public class PolicyRequiredDocumentEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "product_config_id", nullable = false)
    private String productConfigId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 32)
    private RequiredDocumentType documentType;

    @Column(nullable = false)
    private boolean mandatory;

    public static PolicyRequiredDocumentEntity toEntity(PolicyRequiredDocument model) {
        PolicyRequiredDocumentEntity entity = new PolicyRequiredDocumentEntity();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public PolicyRequiredDocument toDto() {
        PolicyRequiredDocument dto = new PolicyRequiredDocument();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
