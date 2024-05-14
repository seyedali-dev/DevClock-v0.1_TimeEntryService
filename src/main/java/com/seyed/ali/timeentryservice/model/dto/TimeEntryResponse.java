package com.seyed.ali.timeentryservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TimeEntryResponse(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
        String timeEntryId,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Time segments recorded", example = "12345") //TODO: provide example
        List<TimeSegmentDTO> timeSegmentDTOList,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Total time recorded", example = "12345") //TODO: provide example
        String totalDuration
) {
}
