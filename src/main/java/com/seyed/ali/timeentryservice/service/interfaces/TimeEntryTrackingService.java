package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;

import java.math.BigDecimal;

/**
 * Interface for Time Entry tracking service operations.
 */
public interface TimeEntryTrackingService {

    /**
     * Starts tracking a new time entry.
     *
     * @param billable   A boolean indicating whether the time entry is billable or not.
     * @param hourlyRate The hourly rate for the time entry (if billable).
     * @return The ID of the created time entry.
     */
    String startTrackingTimeEntry(boolean billable, BigDecimal hourlyRate);

    /**
     * Stops tracking an existing time entry.
     *
     * @param timeEntryId The ID of the time entry to stop tracking.
     * @return The TimeEntryDTO object representing the stopped time entry.
     */
    TimeEntryDTO stopTrackingTimeEntry(String timeEntryId);

    /**
     * Continues tracking an existing time entry.
     *
     * @param timeEntryId The ID of the time entry to continue tracking.
     * @return The TimeEntryDTO object representing the continued time entry.
     */
    TimeEntryDTO continueTrackingTimeEntry(String timeEntryId);

}
