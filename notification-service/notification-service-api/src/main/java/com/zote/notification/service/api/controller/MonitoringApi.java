package com.zote.notification.service.api.controller;

import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.response.DLQItemResponse;
import com.zote.notification.service.api.response.NotificationMetricsResponse;
import com.zote.notification.service.api.response.ProviderHealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Monitoring API")
@RequestMapping("/notification/monitoring")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface MonitoringApi {
    @Operation(summary = "Get notification metrics")
    @GetMapping("/metrics")
    NotificationMetricsResponse getMetrics(
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate);
    
    @Operation(summary = "Get provider health")
    @GetMapping("/providers/health")
    List<ProviderHealthResponse> getProviderHealth();
    
    @Operation(summary = "Get DLQ items")
    @GetMapping("/dlq")
    PageResponse getDLQItems(
            @RequestParam(required = false) Boolean processed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);
    
    @Operation(summary = "Retry DLQ item")
    @PostMapping("/dlq/{dlqId}/retry")
    void retryDLQItem(@PathVariable Long dlqId);
    
    @Operation(summary = "Delete DLQ item")
    @DeleteMapping("/dlq/{dlqId}")
    void deleteDLQItem(@PathVariable Long dlqId);
    
    @Operation(summary = "Reprocess all DLQ items")
    @PostMapping("/dlq/reprocess-all")
    void reprocessAllDLQItems();

    @Operation(summary = "Get notification delivery logs with filters")
    @GetMapping("/delivery-logs")
    PageResponse getDeliveryLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get delivery log details by notification ID")
    @GetMapping("/delivery-logs/{notificationId}")
    Object getDeliveryLogDetails(@PathVariable String notificationId);

    @Operation(summary = "Export delivery logs to CSV")
    @GetMapping("/delivery-logs/export")
    String exportDeliveryLogsCsv(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate);

    @Operation(summary = "Get delivery statistics")
    @GetMapping("/delivery-stats")
    Object getDeliveryStats(
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate);
}
