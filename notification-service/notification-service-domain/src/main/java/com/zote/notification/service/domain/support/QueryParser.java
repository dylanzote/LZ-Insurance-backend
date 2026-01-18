package com.zote.notification.service.domain.support;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class QueryParser {

    public NotificationChannel parseChannel(String channel) {
        if (channel == null || channel.isEmpty()) {
            return null;
        }
        try {
            return NotificationChannel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid channel value: {}", channel);
            return null;
        }
    }

    public LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime);
        } catch (Exception e) {
            log.warn("Invalid dateTime format: {}", dateTime);
            return null;
        }
    }

    public LocalDateTime parseDateTimeWithDefault(String dateTime, LocalDateTime defaultValue) {
        LocalDateTime parsed = parseDateTime(dateTime);
        return parsed != null ? parsed : defaultValue;
    }

    public LocalTime parseTime(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            log.warn("Invalid time format: {}, expected HH:mm", time);
            return null;
        }
    }
}

