package com.seyed.ali.timeentryservice.model.dto;

import com.seyed.ali.timeentryservice.annotation.OptionalField;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO for {@link TimeEntry}
 */
@Schema(description = "Time Entry Data Transfer Object")
public record TimeEntryDTO(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the time entry", example = "12345")
        @Size(max = 36, message = "timeEntryId must be maximum 36 characters")
        String timeEntryId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Start time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 08:00:00")
        @NotBlank(message = "startTime is mandatory") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "startTime must be in the format yyyy-MM-dd HH:mm:ss")
        String startTime,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "End time of the time entry in the format yyyy-MM-dd HH:mm", example = "2024-05-12 10:00:00")
        @NotBlank(message = "endTime is mandatory")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "endTime must be in the format yyyy-MM-dd HH:mm:ss")
        String endTime,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A flag determining this time entry is billable", example = "true")
        boolean billable,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The hourly rate in BigDecimal format", example = "10.0")
        @DecimalMin(value = "0.0", inclusive = false, message = "hourlyRate must be greater than 0")
        @DecimalMax(value = "999.99", message = "hourlyRate must be less than 1000")
        @OptionalField
        String hourlyRate,

        @Schema(requiredMode = Schema.RequiredMode.AUTO, description = "Duration of the time entry in the format HH:mm:ss", example = "02:00:00")
        @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "duration must be in the format HH:mm:ss")
        String duration
) {
}
