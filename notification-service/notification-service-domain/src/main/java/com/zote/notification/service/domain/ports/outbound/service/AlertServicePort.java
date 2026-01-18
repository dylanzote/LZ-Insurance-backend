package com.zote.notification.service.domain.ports.outbound.service;


import com.zote.notification.service.domain.model.Alert;

import java.util.List;

public interface AlertServicePort {
    void checkErrorRateAlerts();
    void checkLatencyAlerts();
    void checkQueueDepthAlerts();
    void checkProviderHealthAlerts();
    void sendAlert(Alert alert);
    List<Alert> getRecentAlerts(int limit);
}
