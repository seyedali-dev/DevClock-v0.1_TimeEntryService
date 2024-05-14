package com.seyed.ali.timeentryservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/*public record TimeEntryDTO(
        String id,
        String startTime, // in a specific format, e.g., "yyyy-MM-dd HH:mm"
        String endTime, // in a specific format, e.g., "yyyy-MM-dd HH:mm"
        String duration // in a specific format, e.g., "HH:mm:ss"
) {
}*/
@Schema(description = "Time Entry Data Transfer Object")
public record TimeEntryDTO(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
        String timeEntryId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 08:00:00")
        String startTime,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "End time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 10:00:00")
        String endTime,

        @Schema(requiredMode = Schema.RequiredMode.AUTO, description = "Duration of the time entry in the format HH:mm:ss", example = "02:00:00")
        String duration
) {
}
