package com.zote.policy.service.infrastructure.outbound.document;

import com.zote.common.utils.document.DocumentGenerationRequest;
import com.zote.common.utils.document.DocumentGenerationResult;
import com.zote.common.utils.document.DocumentGenerationService;
import com.zote.common.utils.document.DocumentMetadata;
import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.ports.outbound.PolicyDocumentGeneratorPort;
import com.zote.policy.service.domain.support.DocumentBuilderSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyDocumentGeneratorPortImpl implements PolicyDocumentGeneratorPort {

    private static final String DOCUMENT_NAME_PREFIX = "Policy-Schedule";
    private static final String SERVICE_NAME = "policy-service";
    private static final String DOCUMENT_TYPE = "POLICY_SCHEDULE";

    private final DocumentGenerationService documentGenerationService;

    @Override
    public PolicyDocument generatePolicyScheduleDocument(Policy policy) {
        log.info("Generating policy schedule PDF for policy {} via generic document service", policy.getPolicyNumber());

        Map<String, Object> data = buildTemplateData(policy);
        DocumentMetadata metadata = DocumentMetadata.builder()
                .serviceName(SERVICE_NAME)
                .documentType(DOCUMENT_TYPE)
                .entityId(policy.getId())
                .fileName(DOCUMENT_NAME_PREFIX + "-" + policy.getPolicyNumber() + ".pdf")
                .build();

        DocumentGenerationRequest request = DocumentGenerationRequest.builder()
                .templatePath("templates/documents/policy-schedule.html")
                .data(data)
                .metadata(metadata)
                .outputFormat("pdf")
                .build();

        DocumentGenerationResult result = documentGenerationService.generate(request);

        return DocumentBuilderSupport.buildGeneratedPolicyScheduleDocument(
                policy.getId(),
                DOCUMENT_NAME_PREFIX + "-" + policy.getPolicyNumber() + ".pdf",
                result.getDocumentUrl()
        );
    }

    private Map<String, Object> buildTemplateData(Policy policy) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        Map<String, Object> data = new HashMap<>();
        data.put("policyNumber", policy.getPolicyNumber());
        data.put("customerId", policy.getCustomerId());
        data.put("productId", policy.getProductId());
        data.put("policyType", policy.getType() != null ? policy.getType().name() : "N/A");
        data.put("effectiveDate", policy.getEffectiveDate() != null ? policy.getEffectiveDate().format(formatter) : "N/A");
        data.put("expiryDate", policy.getExpiryDate() != null ? policy.getExpiryDate().format(formatter) : "N/A");
        data.put("premiumTotal", policy.getPremiumTotal() != null ? policy.getPremiumTotal() : "N/A");
        data.put("currency", policy.getCurrency() != null ? policy.getCurrency() : "");
        data.put("billingPlan", policy.getBillingPlan() != null ? policy.getBillingPlan().name() : "N/A");
        data.put("generatedAt", java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return data;
    }
}
