package com.zote.common.utils.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Slf4j
@AutoConfiguration
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MetricsProperties.class)
@ConditionalOnClass({PrometheusMeterRegistry.class, MeterRegistry.class})
public class MetricsAutoConfiguration {

    private final MetricsProperties metricsProperties;
    private final Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public PrometheusMeterRegistry prometheusMeterRegistry(PrometheusConfig config, PrometheusRegistry prometheusRegistry) {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(config, prometheusRegistry, Clock.SYSTEM);

        // Configure common tags
        registry.config().commonTags(
            "application", environment.getProperty("spring.application.name", "unknown"),
            "environment", environment.getProperty("spring.profiles.active", "default"),
            "instance", environment.getProperty("eureka.instance.instance-id", environment.getProperty("HOSTNAME", "unknown"))
        );

        log.info("Prometheus Meter Registry initialized for application: {}", environment.getProperty("spring.application.name"));

        return registry;
    }

    @Bean
    @ConditionalOnMissingBean
    public PrometheusConfig prometheusConfig() {
        return new PrometheusConfig() {
            @Override
            public Duration step() {
                return Duration.ofSeconds(metricsProperties.getStepSeconds());
            }

            @Override
            public String get(String key) {
                return null; // Use defaults
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public CollectorRegistry collectorRegistry() {
        return new CollectorRegistry(true);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            // Add common tags to all metrics
            registry.config()
                .commonTags(
                    "region", metricsProperties.getRegion(),
                    "zone", metricsProperties.getZone(),
                    "version", metricsProperties.getVersion()
                );

            // Configure histogram percentiles for timers
            registry.config().meterFilter(
                MeterFilter.maxExpected("http.server.requests", Long.MAX_VALUE)
            );

            // Enable histogram for all timers
            registry.config().meterFilter(new MeterFilter() {
                @Override
                public DistributionStatisticConfig configure(Meter.Id id,
                                                           DistributionStatisticConfig config) {
                    if (id.getType() == Meter.Type.TIMER ||
                        id.getType() == Meter.Type.DISTRIBUTION_SUMMARY) {
                        return DistributionStatisticConfig.builder()
                            .percentiles(
                                metricsProperties.getPercentiles()
                                    .stream()
                                    .mapToDouble(Double::doubleValue)
                                    .toArray()
                            )
                            .percentilePrecision(metricsProperties.getPercentilePrecision())
                            .minimumExpectedValue(metricsProperties.getMinimumExpectedValue())
                            .maximumExpectedValue(metricsProperties.getMaximumExpectedValue())
                            .expiry(Duration.ofSeconds(metricsProperties.getStepSeconds() * 2))
                            .bufferLength(metricsProperties.getBufferLength())
                            .build()
                            .merge(config);
                    }
                    return config;
                }
            });
        };
    }

    @Bean
    public MeterFilter renameMeterFilter() {
        return MeterFilter.renameTag(
            metricsProperties.getApplicationName(),
            "application.name",
            "app"
        );
    }
}
