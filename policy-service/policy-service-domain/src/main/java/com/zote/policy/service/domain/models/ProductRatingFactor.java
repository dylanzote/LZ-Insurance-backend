package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRatingFactor {
    private String id;
    private String productId;
    private String code;
    private String label;
    private DataType dataType;
    private boolean required;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
