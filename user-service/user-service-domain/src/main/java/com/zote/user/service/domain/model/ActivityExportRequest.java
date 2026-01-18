package com.zote.user.service.domain.model;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Data
public class ActivityExportRequest {
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxRecords = 1000;
    private String sortBy = "createdAt";
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private ExportFormat format = ExportFormat.CSV;

    // CSV specific options
    private boolean includeBom = true;
    private boolean quoteAllFields = false;
    private boolean skipHeaders = false;
    private String delimiter = ",";
    private String lineSeparator = "\r\n";

    public Pageable getPageable() {
        return PageRequest.of(0, maxRecords, sortDirection, sortBy);
    }

    public enum ExportFormat {
        CSV, JSON, XML
    }
}
