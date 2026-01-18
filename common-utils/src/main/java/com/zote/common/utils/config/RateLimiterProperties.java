package com.zote.common.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notification.rate-limiting")
@Data
public class RateLimiterProperties {
    private ChannelLimits email = new ChannelLimits(100);
    private ChannelLimits sms = new ChannelLimits(50);
    private ChannelLimits push = new ChannelLimits(1000);
    private ChannelLimits webhook = new ChannelLimits(500);
    private long defaultMaxPerMinute = 100;
}
