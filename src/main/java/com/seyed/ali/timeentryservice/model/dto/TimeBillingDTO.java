package com.seyed.ali.timeentryservice.model.dto;

import com.seyed.ali.timeentryservice.annotation.OptionalField;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO for {@link TimeEntry}
 */
@Builder
public record TimeBillingDTO(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A flag determining this time entry is billable", example = "true")
        @OptionalField
        boolean billable,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The hourly rate in BigDecimal format", example = "10.0")
        @DecimalMin(value = "0.0", inclusive = false, message = "hourlyRate must be greater than 0")
        @DecimalMax(value = "999.99", message = "hourlyRate must be less than 1000")
        @OptionalField
        BigDecimal hourlyRate
) {
}
