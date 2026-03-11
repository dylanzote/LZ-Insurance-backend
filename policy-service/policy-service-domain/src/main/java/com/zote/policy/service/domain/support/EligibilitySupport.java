package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.EligibilityResult;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EligibilitySupport {

    public EligibilityResult evaluate(CreateQuoteData data, PolicyProductConfig config) {
        // placeholder rules
        // hard fail / manual review / eligible
        return EligibilityResult.builder()
                .eligible(true)
                .requiresManualReview(config.isRequiresUnderwriting())
                .reason(null)
                .build();
    }
}
