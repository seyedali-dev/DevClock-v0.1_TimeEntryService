package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;

import java.math.BigDecimal;

public interface TimeEntryTrackingService {

    String startTrackingTimeEntry(boolean billable, BigDecimal hourlyRate);

    TimeEntryDTO stopTrackingTimeEntry(String timeEntryId);

    TimeEntryDTO continueTrackingTimeEntry(String timeEntryId);

}
