package com.zote.policy.service.domain.ports.outbound;

import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;

public interface EventPublisherPort {

    void publishPolicyCreated(PolicyCreatedEvent event);

    void publishPolicyIssuedEvent(PolicyIssuedEvent event);

//    void publishPolicyUpdated(PolicyUpdatedEvent event);
//
//    void publishPolicyCancelled(PolicyCancelledEvent event);
//
//    void publishPolicyRenewed(PolicyRenewedEvent event);
//
//    void publishPolicyEndorsed(PolicyEndorsedEvent event);
}
