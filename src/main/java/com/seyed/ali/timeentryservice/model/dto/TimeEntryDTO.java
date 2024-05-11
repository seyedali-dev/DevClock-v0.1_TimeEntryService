package com.seyed.ali.timeentryservice.model.dto;

public record TimeEntryDTO(
        String id,
        String startTime, // in a specific format, e.g., "yyyy-MM-dd HH:mm"
        String endTime, // in a specific format, e.g., "yyyy-MM-dd HH:mm"
        String duration // in a specific format, e.g., "HH:mm:ss" or "H hours M minutes S seconds"
) {
}
