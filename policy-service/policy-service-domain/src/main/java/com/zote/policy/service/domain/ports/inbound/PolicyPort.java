package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.data.CreatePolicyData;
import com.zote.policy.service.domain.models.data.IssuePolicyData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PolicyPort {

    Policy createPolicy(CreatePolicyData data);

    Policy issuePolicy(IssuePolicyData data);

//    Policy cancelPolicy(String policyId, CancelPolicyData data);
//
//    Policy suspendPolicy(String policyId, SuspendPolicyData data);
//
//    Policy reinstatePolicy(String policyId, ReinstatePolicyData data);
//
//    Policy renewPolicy(String policyId, RenewPolicyData data);
//
//    Policy findPolicyById(String policyId);
//
//    Policy findByPolicyNumber(String policyNumber);
//
//    Page<Policy> listPolicies(PolicySearchCriteria criteria, int page, int size, String sortField, Sort.Direction direction);
//
//    Page<Policy> listPoliciesByCustomer(String customerId, int page, int size, String sortField, Sort.Direction direction);
//
//    PolicySummary getCustomerPolicySummary(String customerId);
//
//    List<Policy> getRenewalReminders(int daysAhead);
}
