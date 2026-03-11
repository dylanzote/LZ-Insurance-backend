package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.DataType;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class ProductRatingFactorResponse {

    private String id;
    private String productId;
    private String code;
    private String label;
    private DataType dataType;
    private boolean required;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductRatingFactorResponse from(ProductRatingFactor factor) {
        ProductRatingFactorResponse response = new ProductRatingFactorResponse();
        BeanUtils.copyProperties(factor, response);
        return response;
    }
}
