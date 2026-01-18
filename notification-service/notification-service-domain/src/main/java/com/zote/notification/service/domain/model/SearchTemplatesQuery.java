package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchTemplatesQuery {
    private NotificationChannel channel;
    private Boolean isActive;
    private String name;
    private int page;
    private int size;
}
