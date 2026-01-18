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
public class ProviderHealthMessage {
    private String providerId;
    private String providerName;
    private String status;
    private Double errorRate;
    private Integer latencyMs;
    private LocalDateTime timestamp;
}
