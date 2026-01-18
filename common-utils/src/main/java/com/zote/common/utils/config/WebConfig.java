package com.zote.common.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();

        FilterRegistrationBean<ForwardedHeaderFilter> registration =
            new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }

    public String getClientIpAddress() {
        try {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletAttrs) {
                HttpServletRequest request = servletAttrs.getRequest();
                String ip = request.getRemoteAddr();
                if (isValidPublicIp(ip)) {
                    return ip;
                }
                return ip;
            }
        } catch (Exception e) {
            log.error("Could not get client IP address: {}", e.getMessage());
        }

        return "unknown";
    }

    private boolean isValidPublicIp(String ip) {
        try {
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        InetAddress address = InetAddress.getByName(ip);
        return !address.isLoopbackAddress() &&
               !address.isAnyLocalAddress() &&
               !address.isSiteLocalAddress();
        } catch (Exception e) {
            return false;
        }
    }
}
