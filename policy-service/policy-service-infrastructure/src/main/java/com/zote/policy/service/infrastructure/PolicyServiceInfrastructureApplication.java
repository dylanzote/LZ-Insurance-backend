package com.zote.policy.service.infrastructure;

import com.zote.policy.service.infrastructure.config.PolicyDefaultsConfig;
import com.zote.policy.service.infrastructure.config.ProductConfigSeedProperties;
import com.zote.policy.service.infrastructure.config.RatingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.zote.*"})
@EnableJpaAuditing
@EnableConfigurationProperties({PolicyDefaultsConfig.class, RatingConfig.class, ProductConfigSeedProperties.class})
public class PolicyServiceInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyServiceInfrastructureApplication.class, args);
    }

}
