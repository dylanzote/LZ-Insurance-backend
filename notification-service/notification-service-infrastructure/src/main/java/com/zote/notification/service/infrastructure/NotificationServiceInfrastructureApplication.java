package com.zote.notification.service.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = {"com.zote.*"})
@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableRetry
@EnableCaching
public class NotificationServiceInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceInfrastructureApplication.class, args);
    }

}
