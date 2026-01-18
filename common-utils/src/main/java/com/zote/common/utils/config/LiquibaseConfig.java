package com.zote.common.utils.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
@AllArgsConstructor
public class LiquibaseConfig {

    private final LiquibaseProperties liquibaseProperties;

    private final DataSource dataSource;

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        return liquibase;
    }
}
