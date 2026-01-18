package com.zote.resourcebundle.adapter.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zote.*"})
@EnableJpaAuditing
public class ResourcebundleAdapterInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourcebundleAdapterInfrastructureApplication.class, args);
    }

}
