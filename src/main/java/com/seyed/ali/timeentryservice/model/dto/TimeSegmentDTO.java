package com.seyed.ali.timeentryservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.seyed.ali.timeentryservice.model.domain.TimeSegment}
 */
@Builder
public record TimeSegmentDTO(String timeSegmentId,
                             @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 08:00:00")
                             String startTime,

                             @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "End time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 10:00:00")
                             String endTime,

                             @Schema(requiredMode = Schema.RequiredMode.AUTO, description = "Duration of the time entry in the format HH:mm:ss", example = "02:00:00")
                             String duration,

                             @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
                             String timeEntryId,
                             @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the user associated with time entry", example = "12345")
                             String userId) implements Serializable {
}