package com.zote.user.service.api.response;

import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {
    private String id;
    private String userId;
    private String action;
    private String resource;
    private String resourceId;
    private String ipAddress;
    private LocalDateTime createdDate;

    public static UserActivityResponse toResponse(UserActivity userActivity) {
        UserActivityResponse userActivityResponse = new UserActivityResponse();
        BeanUtils.copyProperties(userActivity, userActivityResponse);
        return userActivityResponse;
    }
}

