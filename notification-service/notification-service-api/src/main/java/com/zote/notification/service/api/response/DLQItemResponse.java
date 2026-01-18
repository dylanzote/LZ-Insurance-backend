package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.DeadLetterQueueItem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class DLQItemResponse {
    private Long id;
    private String notificationId;
    private String providerId;
    private String errorType;
    private String errorMessage;
    private Integer retryCount;
    private Boolean processed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledRetryAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static DLQItemResponse fromDomain(DeadLetterQueueItem item) {
        var response = new DLQItemResponse();
        BeanUtils.copyProperties(item, response);
        return response;
    }
}
