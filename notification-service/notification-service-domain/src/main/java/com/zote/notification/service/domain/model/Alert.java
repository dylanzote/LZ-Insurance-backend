package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.AlertSeverity;
import com.zote.common.utils.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private String id;
    private AlertType type;
    private AlertSeverity severity;
    private String title;
    private String message;
    private String entityId;
    private String entityType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
