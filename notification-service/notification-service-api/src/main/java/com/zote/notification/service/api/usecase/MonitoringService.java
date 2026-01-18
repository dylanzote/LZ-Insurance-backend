package com.zote.notification.service.api.usecase;

import com.zote.notification.service.api.controller.MonitoringApi;
import com.zote.notification.service.api.request.GetDLQItemsRequest;
import com.zote.notification.service.api.request.GetMetricsRequest;
import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.response.DLQItemResponse;
import com.zote.notification.service.api.response.NotificationMetricsResponse;
import com.zote.notification.service.api.response.ProviderHealthResponse;
import com.zote.notification.service.domain.model.GetMetricsQuery;
import com.zote.notification.service.domain.ports.inbound.MonitorNotificationsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MonitoringService implements MonitoringApi {

    private final MonitorNotificationsPort monitorNotificationsPort;

    @Override
    public NotificationMetricsResponse getMetrics(String channel, String fromDate, String toDate) {
        log.info("API: Getting metrics, channel: {}", channel);
        var request = new GetMetricsRequest();
        request.setChannel(channel);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        var query = request.toGetMetricsQuery();
        var metrics = monitorNotificationsPort.getMetrics(query);
        return NotificationMetricsResponse.fromDomain(metrics);
    }

    @Override
    public List<ProviderHealthResponse> getProviderHealth() {
        log.info("API: Getting provider health");
        var healthMap = monitorNotificationsPort.getProviderHealthMetrics();
        return healthMap.values().stream()
            .map(ProviderHealthResponse::fromDomain)
            .toList();
    }

    @Override
    public PageResponse getDLQItems(Boolean processed, int page, int size) {
        log.info("API: Getting DLQ items, processed: {}", processed);
        var request = new GetDLQItemsRequest();
        request.setProcessed(processed);
        request.setPage(page);
        request.setSize(size);
        var query = request.toGetDLQQuery();
        var result = monitorNotificationsPort.getDeadLetterQueue(query);
        
        var content = result.getItems().stream()
            .map(DLQItemResponse::fromDomain)
            .map(item -> (Object) item)
            .toList();
        
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var pageResult = new org.springframework.data.domain.PageImpl<>(
            content, pageable, result.getTotalElements());
        
        return new PageResponse(pageResult);
    }

    @Override
    public void retryDLQItem(Long dlqId) {
        log.info("API: Retrying DLQ item: {}", dlqId);
        monitorNotificationsPort.retryDLQItem(dlqId);
    }

    @Override
    public void deleteDLQItem(Long dlqId) {
        log.info("API: Deleting DLQ item: {}", dlqId);
        monitorNotificationsPort.deleteDLQItem(dlqId);
    }

    @Override
    public void reprocessAllDLQItems() {
        log.info("API: Reprocessing all DLQ items");
        monitorNotificationsPort.reprocessAllDLQItems();
    }

    // P3.4: Delivery Logs API
    @Override
    public PageResponse getDeliveryLogs(String userId, String channel, String status,
                                         String startDate, String endDate, int page, int size) {
        log.info("API: Getting delivery logs - P3.4 feature stub");
        log.warn("P3.4: Delivery logs API requires full implementation");
        return new PageResponse(List.of(), 0, 0, 0, false, false, false, false);
    }

    @Override
    public Object getDeliveryLogDetails(String notificationId) {
        log.info("API: Getting delivery log details for: {}", notificationId);
        return monitorNotificationsPort.getDeliveryLogDetails(notificationId);
    }

    @Override
    public String exportDeliveryLogsCsv(String userId, String channel, String status,
                                         String startDate, String endDate) {
        log.info("API: Exporting delivery logs to CSV");
        return monitorNotificationsPort.exportDeliveryLogsCsv(userId, channel, status, startDate, endDate);
    }

    @Override
    public Object getDeliveryStats(String channel, String startDate, String endDate) {
        log.info("API: Getting delivery stats");
        return monitorNotificationsPort.getDeliveryStats(channel, startDate, endDate);
    }
}
