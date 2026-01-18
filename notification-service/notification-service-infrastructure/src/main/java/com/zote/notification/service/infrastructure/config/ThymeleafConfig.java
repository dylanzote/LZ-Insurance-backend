package com.zote.notification.service.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

/**
 * Thymeleaf Configuration for Email Templates
 * Configures template resolver and i18n message source
 */
@Configuration
public class ThymeleafConfig {

    /**
     * Configure MessageSource for i18n support
     * Loads messages_en.properties and messages_fr.properties
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    /**
     * Configure Thymeleaf Template Resolver
     * Loads templates from classpath:templates/
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(true);
        templateResolver.setCacheTTLMs(3600000L); // Cache for 1 hour
        return templateResolver;
    }

    /**
     * Configure Thymeleaf Template Engine
     * Integrates template resolver and message source
     */
    @Bean
    public SpringTemplateEngine templateEngine(
            SpringResourceTemplateResolver templateResolver,
            MessageSource messageSource) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setTemplateEngineMessageSource(messageSource);
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }
}

