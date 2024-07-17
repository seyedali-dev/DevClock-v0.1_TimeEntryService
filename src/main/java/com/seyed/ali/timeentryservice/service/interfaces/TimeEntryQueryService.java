package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;

import java.util.List;

/**
 * Interface for Time Entry service operations.
 */
public interface TimeEntryQueryService {

    /**
     * Retrieves a list of all time entries.
     *
     * @return A list of TimeEntryResponse objects representing time entries.
     */
    List<TimeEntry> getTimeEntries();

    /**
     * Retrieves a time entry for a specific user.
     *
     * @param userId The ID of the user whose time entry is to be retrieved.
     * @return A TimeEntryResponse object representing the user's time entry.
     * @throws ResourceNotFoundException if the user is not found.
     */
    TimeEntry getUsersTimeEntry(String userId);

    /**
     * Retrieves a specific time entry.
     *
     * @param timeEntryId The ID of the time entry.
     * @return A TimeEntryResponse object representing the found time entry.
     * @throws ResourceNotFoundException if the time entry is not found.
     */
    TimeEntry getTimeEntryById(String timeEntryId);

    /**
     * Adds a new time entry manually.
     *
     * @param timeEntryDTO The TimeEntryDTO object containing the time entry details.
     * @return A string representing the parsed time information of the added time entry.
     */
    String addTimeEntryManually(TimeEntryDTO timeEntryDTO);

    /**
     * Updates an existing time entry manually.
     *
     * @param id           The ID of the time entry to be updated.
     * @param timeEntryDTO The TimeEntryDTO object containing the updated time entry details.
     * @return The updated TimeEntryDTO object.
     * @throws IllegalArgumentException if the provided ID does not exist.
     */
    TimeEntry updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO);

    /**
     * Deletes a time entry by ID.
     *
     * @param timeEntryId The ID of the time entry to be deleted.
     */
    void deleteTimeEntry(String timeEntryId);

    /**
     * Deletes a time entry.
     *
     * @param timeEntry The time entry to be deleted.
     */
    void deleteTimeEntry(TimeEntry timeEntry);

}
