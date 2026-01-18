package com.zote.user.service.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class UserPageResponse {
    private List<UserResponse> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    @JsonProperty("isFirst")
    private boolean isFirst;
    @JsonProperty("isLast")
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;



    public UserPageResponse(Page<UserResponse> userPage) {
        this(userPage.getContent(), userPage.getTotalElements(), userPage.getTotalPages(), userPage.getNumber() + 1, userPage.isFirst(), userPage.isLast(), userPage.hasNext(), userPage.hasPrevious());
    }
}
