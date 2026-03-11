package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingSchedule;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BillingScheduleResponse {

    private String id;
    private String policyId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BillingStatus status;
    private LocalDateTime paidAt;

    public static BillingScheduleResponse fromBillingSchedule(BillingSchedule schedule) {
        BillingScheduleResponse response = new BillingScheduleResponse();
        BeanUtils.copyProperties(schedule, response);
        return response;
    }
}
