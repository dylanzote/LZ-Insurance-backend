package com.zote.policy.service.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configurable rating formula constants.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "policy.rating")
public class RatingConfig {

    private BigDecimal basePremium = new BigDecimal("10000");
    private BigDecimal defaultLimit = new BigDecimal("100000");
    private BigDecimal deductibleDiscountFactor = new BigDecimal("0.95");
    private BigDecimal taxRate = new BigDecimal("0.1925");
}
