package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.RenewalResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RenewalResultResponse {

    private String policyId;
    private String renewalQuoteId;
    private BigDecimal renewalPremium;
    private LocalDate newExpiryDate;

    public static RenewalResultResponse fromRenewalResult(RenewalResult result) {
        RenewalResultResponse response = new RenewalResultResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }
}
