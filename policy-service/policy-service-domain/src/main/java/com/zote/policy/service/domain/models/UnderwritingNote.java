package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingNote {
    private String id;
    private String quoteId;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;
}
