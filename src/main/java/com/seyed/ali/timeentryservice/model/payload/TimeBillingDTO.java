package com.seyed.ali.timeentryservice.model.payload;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for {@link TimeEntry}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeBillingDTO {

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A flag determining this time entry is billable", example = "true")
        private boolean billable;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The hourly rate in BigDecimal format", example = "10.0")
        @DecimalMin(value = "0.0", inclusive = false, message = "hourlyRate must be greater than 0")
        @DecimalMax(value = "999.99", message = "hourlyRate must be less than 1000")
        private BigDecimal hourlyRate;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The unique identifier of associated project", example = "12345")
        private String projectId;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The unique identifier of associated task", example = "12345")
        private String taskId;

}
