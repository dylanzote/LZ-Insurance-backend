package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.GetMetricsQuery;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class GetMetricsRequest {
    private String channel; // Will be converted to NotificationChannel in domain layer
    private String fromDate; // Will be parsed to LocalDateTime in domain layer
    private String toDate; // Will be parsed to LocalDateTime in domain layer

    public GetMetricsQuery toGetMetricsQuery() {
        var query = new GetMetricsQuery();
        query.setChannel(channel);
        query.setFromDate(fromDate);
        query.setToDate(toDate);
        return query;
    }
}

