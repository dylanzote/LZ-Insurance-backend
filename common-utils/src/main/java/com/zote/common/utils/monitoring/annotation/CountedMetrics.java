package com.zote.common.utils.monitoring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountedMetrics {

    String name() default "";
    String description() default "";
    String[] extraTags() default {};
    CountType type() default CountType.INCREMENT;

    enum CountType {
        INCREMENT,
        DECREMENT
    }
}
