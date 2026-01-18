package com.zote.user.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.files.CsvUtils;
import com.zote.user.service.domain.model.ActivityExportRequest;
import com.zote.user.service.domain.model.UserActivity;
import com.zote.user.service.domain.ports.outbound.UserActivityRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityExportService {

    private final UserActivityRepositoryPort userActivityRepositoryPort;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dateInputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Export activities to CSV with filtering options
     */
    public byte[] exportActivitiesToCsv(ActivityExportRequest request) {
        log.info("Exporting activities to CSV with request: {}", request);

        validateExportRequest(request);

        List<UserActivity> activities = fetchActivities(request);
        activities = filterActivitiesByDateRange(activities, request);

        return generateCsv(activities, request);
    }

    /**
     * Export activities to CSV string
     */
    public String exportActivitiesToCsvString(ActivityExportRequest request) {
        log.info("Exporting activities to CSV string with request: {}", request);

        validateExportRequest(request);

        List<UserActivity> activities = fetchActivities(request);
        activities = filterActivitiesByDateRange(activities, request);

        return generateCsvString(activities, request);
    }

    /**
     * Export activities with simple parameters (backward compatibility)
     */
    public String exportActivitiesToCsv(String userId, String startDate, String endDate) {
        log.info("Exporting activities to CSV - userId: {}, startDate: {}, endDate: {}",
                userId, startDate, endDate);

        ActivityExportRequest request = createExportRequest(userId, startDate, endDate);
        return exportActivitiesToCsvString(request);
    }

    /**
     * Parse date string with validation
     */
    public LocalDateTime parseDateString(String dateString) {
        if (!StringUtils.hasText(dateString)) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateString, dateInputFormatter);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date string: {}", dateString, e);
            throw new FunctionalError(SecurityConstants.ERROR_INVALID_DATE_FORMAT);
        }
    }

    // Private helper methods

    private ActivityExportRequest createExportRequest(String userId, String startDate, String endDate) {
        ActivityExportRequest request = new ActivityExportRequest();
        request.setUserId(userId);
        request.setStartDate(parseDateString(startDate));
        request.setEndDate(parseDateString(endDate));
        request.setMaxRecords(10000); // Default limit
        request.setIncludeBom(true);
        return request;
    }

    private void validateExportRequest(ActivityExportRequest request) {
        if (request.getMaxRecords() > 10000) {
            throw new FunctionalError(SecurityConstants.ERROR_EXPORT_RECORD_LIMIT_EXCEEDED);
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new FunctionalError(SecurityConstants.ERROR_INVALID_DATE_RANGE);
            }
        }
    }

    private List<UserActivity> fetchActivities(ActivityExportRequest request) {
        // Since your repository doesn't have pagination methods, fetch all and limit manually
        List<UserActivity> activities;
        if (request.getUserId() != null) {
            activities = userActivityRepositoryPort.findActivitiesByUserId(request.getUserId());
        } else {
            activities = userActivityRepositoryPort.findAllActivities();
        }

        // Apply manual limit since no pagination in repository
        if (request.getMaxRecords() != null && activities.size() > request.getMaxRecords()) {
            activities = activities.subList(0, request.getMaxRecords());
        }

        return activities;
    }

    private List<UserActivity> filterActivitiesByDateRange(
            List<UserActivity> activities, ActivityExportRequest request) {

        return activities.stream()
                .filter(activity -> isWithinDateRange(activity, request))
                .toList();
    }

    private boolean isWithinDateRange(UserActivity activity, ActivityExportRequest request) {
        LocalDateTime createdAt = activity.getCreatedAt();
        if (createdAt == null) {
            return false;
        }

        boolean afterStart = request.getStartDate() == null ||
                            !createdAt.isBefore(request.getStartDate());
        boolean beforeEnd = request.getEndDate() == null ||
                           !createdAt.isAfter(request.getEndDate());

        return afterStart && beforeEnd;
    }

    // CSV Generation methods

    private byte[] generateCsv(List<UserActivity> activities, ActivityExportRequest request) {
        return CsvUtils.builder()
                .withData(Collections.singletonList(activities))
                .withHeaders(getCsvHeaders())
//                .withFieldExtractors(getFieldExtractors())
                .withFormat(getCsvFormat(request))
                .withBom(request.isIncludeBom())
                .includeHeaders(!request.isSkipHeaders())
                .buildBytes();
    }

    private String generateCsvString(List<UserActivity> activities, ActivityExportRequest request) {
        return CsvUtils.builder()
                .withData(Collections.singletonList(activities))
                .withHeaders(getCsvHeaders())
//                .withFieldExtractors(getFieldExtractors())
                .withFormat(getCsvFormat(request))
                .withBom(request.isIncludeBom())
                .includeHeaders(!request.isSkipHeaders())
                .buildString();
    }

    private String[] getCsvHeaders() {
        return new String[] {
            "Timestamp",
            "User ID",
            "Action",
            "Resource",
            "Resource ID",
            "IP Address",
            "User Agent",
            "Status",
            "Duration (ms)",
            "Additional Info"
        };
    }

    private List<Function<UserActivity, String>> getFieldExtractors() {
        return List.of(
            this::formatDateTime,
            UserActivity::getUserId,
            UserActivity::getAction,
            UserActivity::getResource,
            this::getResourceId,
            this::getIpAddress,
            this::getUserAgent,
            this::getStatus,
            this::getDuration,
            this::getAdditionalInfo
        );
    }

    private org.apache.commons.csv.CSVFormat getCsvFormat(ActivityExportRequest request) {
        org.apache.commons.csv.CSVFormat baseFormat = CsvUtils.EXCEL_CSV_FORMAT;

        // Customize format based on request
        if (request.isQuoteAllFields()) {
            baseFormat = baseFormat.withQuoteMode(QuoteMode.ALL);
        }

        if (StringUtils.hasText(request.getDelimiter()) && request.getDelimiter().length() == 1) {
            baseFormat = baseFormat.withDelimiter(request.getDelimiter().charAt(0));
        }

        if (StringUtils.hasText(request.getLineSeparator())) {
            baseFormat = baseFormat.withRecordSeparator(request.getLineSeparator());
        }

        return baseFormat;
    }

    // Field extractor helper methods

    private String formatDateTime(UserActivity activity) {
        if (activity.getCreatedAt() == null) {
            return "";
        }
        return activity.getCreatedAt().format(dateFormatter);
    }

    private String getResourceId(UserActivity activity) {
        return activity.getResourceId() != null ? activity.getResourceId() : "";
    }

    private String getIpAddress(UserActivity activity) {
        return activity.getIpAddress() != null ? activity.getIpAddress() : "";
    }

    private String getUserAgent(UserActivity activity) {
        return activity.getUserAgent() != null ? activity.getUserAgent() : "";
    }

    private String getStatus(UserActivity activity) {
        return activity.getStatus() != null ? activity.getStatus() : "";
    }

    private String getDuration(UserActivity activity) {
        if (activity.getDurationMs() == null) {
            return "";
        }
        return activity.getDurationMs().toString();
    }

    private String getAdditionalInfo(UserActivity activity) {
        return activity.getAdditionalInfo() != null ? activity.getAdditionalInfo() : "";
    }
}