package com.seyed.ali.timeentryservice.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeParser {

    public LocalDateTime parseStringToLocalDateTime(String string) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(string, dateTimeFormatter);
    }

    public Duration parseStringToDuration(String string) {
        String[] durationParts = string.split(":");
        long hours = Long.parseLong(durationParts[0]);
        long minutes = Long.parseLong(durationParts[1]);
        long seconds = Long.parseLong(durationParts[2]);
        return Duration
                .ofHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }

    public String parseLocalDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter);
    }

    public String parseDurationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String parseTimeToString(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String startTimeStr = startTime.format(formatter);
        String endTimeStr = endTime.format(formatter);
        String durationStr = parseDurationToString(duration);

        return "startTime (" + startTimeStr + ")" +
                " | endTime (" + endTimeStr + ")" +
                " | duration(" + durationStr + ")";
    }

}
