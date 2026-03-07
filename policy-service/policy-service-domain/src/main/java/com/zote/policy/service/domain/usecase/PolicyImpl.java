package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.data.CreatePolicyData;
import com.zote.policy.service.domain.models.data.IssuePolicyData;
import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import com.zote.policy.service.domain.ports.outbound.*;
import com.zote.policy.service.domain.support.MessagingSupport;
import com.zote.policy.service.domain.support.PolicyBuilderSupport;
import com.zote.policy.service.domain.support.PolicySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PolicyImpl implements PolicyPort {

    private final PolicyRepositoryPort policyRepositoryPort;
    private final PolicyVersionRepositoryPort policyVersionRepositoryPort;
    private final PolicyDocumentRepositoryPort policyDocumentRepositoryPort;
    private final BillingScheduleRepositoryPort billingScheduleRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PolicyStatusHistoryRepositoryPort statusHistoryRepositoryPort;
    private final PolicySupport policySupport;

    private final MessagingSupport messagingSupport;

    @Override
    public Policy createPolicy(CreatePolicyData data) {
        log.info("Creating policy for productId={} customerId={}", data.getProductId(), data.getCustomerId());
        policySupport.validateCreatePolicy(data);
        var policyData = PolicyBuilderSupport.policyBuilder(data);
        var policy = policyRepositoryPort.savePolicy(policyData);
        var policyVersionData = PolicyBuilderSupport.policyVersionBuilder(policy, data.getSnapshot());
        policyVersionRepositoryPort.savePolicyVersion(policyVersionData);
        messagingSupport.publishPolicyCreatedEvent(policy);

        log.info("Policy created: {} number={}", policy.getId(), policy.getPolicyNumber());
        return policy;
    }

    @Override
    public Policy issuePolicy(IssuePolicyData issuePolicyData) {
        log.info("Issuing policy: {}", issuePolicyData.getPolicyId());
        var policy = policyRepositoryPort.findById(issuePolicyData.getPolicyId());
        policySupport.validatingAllowedPolicyStatus(policy.getStatus());
        validatePolicyIssuedPreconditions(issuePolicyData.getPolicyId());

        PolicyStatus from = policy.getStatus();
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setPaymentStatus(PaymentStatus.PAID);

        policyRepositoryPort.updateStatus(issuePolicyData.getPolicyId(), PolicyStatus.ACTIVE);
        statusHistoryRepositoryPort.saveHistory(PolicyBuilderSupport.policyStatusHistoryBuilder(issuePolicyData.getPolicyId(), from));

        // Optional: generate billing schedule if monthly, etc.
        // billingScheduleRepositoryPort.createDefaultSchedule(...)
        messagingSupport.publishPolicyIssuedEvent(policy);

        log.info("Policy issued: {} (from {} -> ACTIVE)", issuePolicyData.getPolicyId(), from);
        return policy;
    }

    private void validatePolicyIssuedPreconditions(String policyId) {
        // 1) Payment completed (depending on your business rules)
        boolean paymentOk = paymentRepositoryPort.hasSuccessfulPayment(policyId);
        if (!paymentOk) {
            throw new FunctionalError("Cannot issue policy: payment not completed");
        }

        // 2) Required documents verified (if your flow requires it)
        boolean docsOk = policyDocumentRepositoryPort.hasAllRequiredDocumentsVerified(policyId);
        if (!docsOk) {
            throw new FunctionalError("Cannot issue policy: documents not verified");
        }
    }
}
