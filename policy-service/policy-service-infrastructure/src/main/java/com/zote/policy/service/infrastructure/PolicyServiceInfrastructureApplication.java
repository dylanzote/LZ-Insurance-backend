package com.zote.policy.service.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zote.*"})
@EnableJpaAuditing
public class PolicyServiceInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyServiceInfrastructureApplication.class, args);
    }

}
