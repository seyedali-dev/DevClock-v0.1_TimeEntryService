package com.seyed.ali.timeentryservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeBillingDTO {

    private boolean billable;
    private BigDecimal hourlyRate;

}
