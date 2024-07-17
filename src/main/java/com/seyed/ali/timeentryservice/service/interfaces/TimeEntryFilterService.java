package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryFilterService {

    /**
     * Fetches the time-entries by project(either it's ID or Name).
     *
     * @param projectCriteria either the ID or the Name of the project.
     * @return List of Found TimeEntries.
     * @throws ResourceNotFoundException If the project is not found.
     */
    List<TimeEntry> getTimeEntriesByProjectCriteria(String projectCriteria) throws ResourceNotFoundException;

    /**
     * Fetches the time-entries by task(Name).
     *
     * @param taskName name of the task.
     * @return List of Found TimeEntries.
     * @throws ResourceNotFoundException If the project is not found.
     */
    List<TimeEntry> getTimeEntriesByTaskName(String taskName);

    /**
     * Fetches the time-entries for the last month.
     *
     * @return List of TimeEntries for the last month.
     */
    List<TimeEntry> getTimeEntriesForLastMonth();

    /**
     * Fetches the time-entries for the last day.
     *
     * @return List of TimeEntries for the last day.
     */
    List<TimeEntry> getTimeEntriesForLastDay();

    /**
     * Fetches the time-entries for the last week.
     *
     * @return List of TimeEntries for the last week.
     */
    List<TimeEntry> getTimeEntriesForLastWeek();

    /**
     * Fetches the time-entries for the last week.
     *
     * @return List of TimeEntries for the last week.
     */
    List<TimeEntry> getTimeEntriesForToday();

    /**
     * Fetches the time-entries for the specified date.
     *
     * @return List of TimeEntries for the specified date.
     */
    List<TimeEntry> getTimeEntriesForSpecifiedDateRange(LocalDate startDate, LocalDate endDate);

}
