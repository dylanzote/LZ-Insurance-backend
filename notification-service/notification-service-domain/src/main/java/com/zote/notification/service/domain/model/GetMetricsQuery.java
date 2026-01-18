package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMetricsQuery {
    private String channel; // String from API, will be parsed
    private String fromDate; // String from API, will be parsed
    private String toDate; // String from API, will be parsed
    private NotificationChannel parsedChannel; // Parsed channel
    private LocalDateTime parsedFromDate; // Parsed fromDate
    private LocalDateTime parsedToDate; // Parsed toDate
}
