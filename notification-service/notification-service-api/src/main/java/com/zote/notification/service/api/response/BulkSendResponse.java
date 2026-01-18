package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.BulkSendResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class BulkSendResponse {
    private String batchId;
    private Integer totalUsers;
    private Integer processedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static BulkSendResponse toBulkSendResponse(BulkSendResult result) {
        var response = new BulkSendResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }
}
