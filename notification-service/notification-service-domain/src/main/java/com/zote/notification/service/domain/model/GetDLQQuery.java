package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDLQQuery {
    private Boolean processed;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int page;
    private int size;
}
