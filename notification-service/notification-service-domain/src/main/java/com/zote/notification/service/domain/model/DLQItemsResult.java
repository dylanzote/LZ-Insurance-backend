package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DLQItemsResult {
    private List<DeadLetterQueueItem> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

