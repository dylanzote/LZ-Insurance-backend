package com.zote.common.utils.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicrometerMetricsService {

    private final MeterRegistry meterRegistry;
}
