package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.Endorsement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EndorsementRepositoryPort {

    Endorsement saveEndorsement(Endorsement endorsement);

    Endorsement findById(String id);

    void deleteById(String id);

    Page<Endorsement> findAllByPolicyId(String policyId, Pageable pageable);

    List<Endorsement> findAllByPolicyIdOrderByEffectiveDateDesc(String policyId);

    Page<Endorsement> findAllByPolicyIdAndType(String policyId, EndorsementType type, Pageable pageable);

    Page<Endorsement> findAllByPolicyIdAndEffectiveDateBetween(String policyId, LocalDate from, LocalDate to, Pageable pageable);
}
