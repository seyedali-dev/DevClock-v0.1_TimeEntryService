package com.seyed.ali.timeentryservice.model.payload.response;

import com.seyed.ali.timeentryservice.model.payload.TimeSegmentDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TimeEntryResponse(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
        String timeEntryId,

        @ArraySchema(schema = @Schema(implementation = TimeSegmentDTO.class))
        List<TimeSegmentDTO> timeSegmentDTOList,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A flag determining this time entry is billable", example = "true")
        boolean billable,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The hourly rate in BigDecimal format", example = "10.0")
        String hourlyRate,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Total time recorded", example = "00:00:18")
        String totalDuration,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for associated project with the time entry", example = "12345")
        String projectId

) {
}