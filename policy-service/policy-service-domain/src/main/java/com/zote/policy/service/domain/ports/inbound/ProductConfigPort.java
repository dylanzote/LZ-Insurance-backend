package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import com.zote.policy.service.domain.models.data.AddRequiredDocumentData;
import com.zote.policy.service.domain.models.data.CreateProductConfigData;
import com.zote.policy.service.domain.models.data.RemoveRequiredDocumentData;
import com.zote.policy.service.domain.models.data.UpdateProductConfigData;

import java.util.List;

public interface ProductConfigPort {

    PolicyProductConfig createProductConfig(CreateProductConfigData data);

    PolicyProductConfig updateProductConfig(UpdateProductConfigData data);

    PolicyProductConfig findById(String id);

    PolicyProductConfig findByProductId(String productId);

    List<PolicyProductConfig> findAllByPolicyType(PolicyType policyType);

    PolicyRequiredDocument addRequiredDocument(AddRequiredDocumentData data);

    void removeRequiredDocument(RemoveRequiredDocumentData data);

    List<PolicyRequiredDocument> getRequiredDocuments(String productConfigId);
}
