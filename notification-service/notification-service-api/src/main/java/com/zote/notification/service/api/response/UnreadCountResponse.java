package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.UnreadCountResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class UnreadCountResponse {
    private String userId;
    private Long unreadCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static UnreadCountResponse toUnreadCountResponse(UnreadCountResult result) {
        var response = new UnreadCountResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }
}
