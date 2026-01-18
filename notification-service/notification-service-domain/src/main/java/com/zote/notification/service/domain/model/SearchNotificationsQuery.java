package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchNotificationsQuery {
    private String userId;
    private String tenantId;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int page;
    private int size;
}
