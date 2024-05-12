package com.seyed.ali.timeentryservice.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeParserUtilForTests {

    public String parseDurationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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

}
