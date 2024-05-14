package com.seyed.ali.timeentryservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Time Entry Data Transfer Object")
public record TimeEntryDTO(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
        String timeEntryId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 08:00:00")
        String startTime,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "End time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 10:00:00")
        String endTime,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A flag determining this time entry is billable", example = "true")
        boolean billable,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The hourly rate in BigDecimal format", example = "10.0")
        String hourlyRate,

        @Schema(requiredMode = Schema.RequiredMode.AUTO, description = "Duration of the time entry in the format HH:mm:ss", example = "02:00:00")
        String duration
) {
}
