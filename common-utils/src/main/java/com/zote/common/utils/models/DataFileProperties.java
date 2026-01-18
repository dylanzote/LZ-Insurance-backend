package com.zote.common.utils.models;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class DataFileProperties {
    private long maxFileSize;
    private long maxRequestSize;
    private long fileSizeThreshold;
}
