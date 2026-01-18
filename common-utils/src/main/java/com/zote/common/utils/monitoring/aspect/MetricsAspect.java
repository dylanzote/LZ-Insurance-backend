package com.zote.common.utils.monitoring.aspect;

import com.zote.common.utils.monitoring.annotation.CountedMetrics;
import com.zote.common.utils.monitoring.annotation.TimedMetrics;
import com.zote.common.utils.monitoring.service.MetricsService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MetricsService metricsService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(timedMetrics)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, TimedMetrics timedMetrics) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Build metric name
        String metricName = timedMetrics.name().isEmpty()
            ? buildDefaultMetricName(method)
            : timedMetrics.name();

        // Build tags from annotation and method parameters
        Map<String, String> tags = buildTags(timedMetrics.extraTags(), joinPoint);

        // Add class and method tags
        tags.put("class", method.getDeclaringClass().getSimpleName());
        tags.put("method", method.getName());

        // Start timer
        Timer.Sample sample = metricsService.startTimer();

        try {
            Object result = joinPoint.proceed();

            // Record success
            tags.put("success", "true");
            metricsService.stopTimer(sample, metricName,
                timedMetrics.description(), tags);

            return result;

        } catch (Exception e) {
            // Record failure
            tags.put("success", "false");
            tags.put("exception", e.getClass().getSimpleName());
            metricsService.stopTimer(sample, metricName,
                timedMetrics.description(), tags);

            // Also record error metric
            metricsService.recordError(
                e.getClass().getSimpleName(),
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                tags
            );

            throw e;
        }
    }

    @Around("@annotation(countedMetrics)")
    public Object countMethodCalls(ProceedingJoinPoint joinPoint, CountedMetrics countedMetrics) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Build metric name
        String metricName = countedMetrics.name().isEmpty()
            ? buildDefaultCounterName(method)
            : countedMetrics.name();

        // Build tags
        Map<String, String> tags = buildTags(countedMetrics.extraTags(), joinPoint);
        tags.put("class", method.getDeclaringClass().getSimpleName());
        tags.put("method", method.getName());

        try {
            Object result = joinPoint.proceed();

            // Record successful invocation
            tags.put("success", "true");
            metricsService.incrementCounter(metricName,
                countedMetrics.description(), tags);

            return result;

        } catch (Exception e) {
            // Record failed invocation
            tags.put("success", "false");
            tags.put("exception", e.getClass().getSimpleName());
            metricsService.incrementCounter(metricName,
                countedMetrics.description(), tags);

            throw e;
        }
    }

    private String buildDefaultMetricName(Method method) {
        return String.format("%s.%s.execution.time",
            method.getDeclaringClass().getSimpleName().toLowerCase(),
            method.getName().toLowerCase());
    }

    private String buildDefaultCounterName(Method method) {
        return String.format("%s.%s.invocations",
            method.getDeclaringClass().getSimpleName().toLowerCase(),
            method.getName().toLowerCase());
    }

    private Map<String, String> buildTags(String[] extraTags,
                                         ProceedingJoinPoint joinPoint) {
        Map<String, String> tags = new HashMap<>();

        if (extraTags.length > 0) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("args", joinPoint.getArgs());

            for (String tag : extraTags) {
                if (tag.contains("=")) {
                    String[] parts = tag.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String valueExpression = parts[1].trim();

                        try {
                            Object value = parser.parseExpression(valueExpression)
                                .getValue(context);
                            if (value != null) {
                                tags.put(key, value.toString());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to parse tag expression: {}", tag, e);
                        }
                    }
                }
            }
        }

        return tags;
    }
}
