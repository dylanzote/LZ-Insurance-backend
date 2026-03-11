package com.zote.policy.service.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configurable product IDs and document types for PolicyProductConfigSeeder.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "policy.seed.product-config")
public class ProductConfigSeedProperties {

    /** Product ID by policy type for seeding. */
    private Map<String, String> productIds = Map.of(
            "AUTO", "AUTO_STD_ANNUAL",
            "HOME", "HOME_STD_ANNUAL",
            "TRAVEL", "TRAVEL_STD_TRIP",
            "HEALTH", "HEALTH_STD_ANNUAL",
            "LIFE", "LIFE_TERM_20",
            "MOTORCYCLE", "MOTO_STD_ANNUAL"
    );

    public String getProductId(String policyType) {
        return productIds.getOrDefault(policyType, policyType + "_STD");
    }
}
