package com.zote.notification.service.api.request;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.CreateProviderData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
public class CreateProviderRequest {

    @NotBlank
    private String name;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private ProviderType providerType;

    @NotNull
    private Map<String, Object> config;

    private Integer priority = 1;

    private Integer maxRatePerMinute;

    private Boolean circuitBreakerEnabled = true;

    private Integer circuitBreakerThreshold = 50;

    public CreateProviderData toCreateProviderData() {
        var createProviderData = new CreateProviderData();
        BeanUtils.copyProperties(this, createProviderData);
        return createProviderData;
    }
}
