package com.zote.common.utils.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse {
    private List<Object> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    @JsonProperty("isFirst")
    private boolean isFirst;
    @JsonProperty("isLast")
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageResponse(Page<Object> userPage) {
        this(userPage.getContent(), userPage.getTotalElements(), userPage.getTotalPages(), userPage.getNumber() + 1, userPage.isFirst(), userPage.isLast(), userPage.hasNext(), userPage.hasPrevious());
    }
}
