package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.CreateProviderData;
import com.zote.notification.service.domain.model.UpdateProviderData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
public class UpdateProviderRequest {

    @NotBlank
    private String providerId;

    private String name;

    private Map<String, Object> config;

    private Integer priority;

    private Boolean isActive;

    private Integer maxRatePerMinute;

    private Boolean circuitBreakerEnabled;

    private Integer circuitBreakerThreshold;

    public UpdateProviderData toUpdateProviderData() {
        var updateProviderData = new UpdateProviderData();
        BeanUtils.copyProperties(this, updateProviderData);
        return updateProviderData;
    }
}
