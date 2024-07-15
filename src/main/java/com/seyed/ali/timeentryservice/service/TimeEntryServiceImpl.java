package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.ProjectServiceClient;
import com.seyed.ali.timeentryservice.client.TaskServiceClient;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.payload.ProjectDTO;
import com.seyed.ali.timeentryservice.model.payload.TaskDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.cache.TimeEntryCacheManager;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeParser timeParser;
    private final TimeEntryUtility timeEntryUtility;
    private final TimeEntryCacheManager timeEntryCacheManager;
    private final ProjectServiceClient projectServiceClient;
    private final TaskServiceClient taskServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntry> getTimeEntries() {
     return this.timeEntryRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#userId",
            unless = "#result == null"
    )
    public TimeEntry getUsersTimeEntry(String userId) {
        return this.timeEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    @Cacheable(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#timeEntryId",
            unless = "#result == null"
    )
    public TimeEntry getTimeEntryById(String timeEntryId) {
        log.info("Db call.");
        TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                .orElseThrow(()-> new ResourceNotFoundException("Time entry with ID: '" + timeEntryId +"' was not found."));
        timeEntry.getTimeSegmentList().size(); // This will initialize the timeSegmentList: otherwise we'll get hibernate's LazyLoadingException.
        return timeEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryUtility.createTimeEntry(timeEntryDTO);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(savedTimeEntry.getTimeEntryId(), savedTimeEntry);

        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        return this.timeParser.parseTimeToString(lastTimeSegment.getStartTime(), lastTimeSegment.getEndTime(), lastTimeSegment.getDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TimeEntry updateTimeEntryManually(String timeEntryId, TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("The provided timeEntryId does not exist"));
        this.timeEntryUtility.updateTimeEntry(timeEntry, timeEntryDTO, this.timeParser);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(timeEntryId, savedTimeEntry);
        return savedTimeEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#timeEntryId"
    )
    public void deleteTimeEntry(String timeEntryId) {
        this.timeEntryRepository.deleteById(timeEntryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#timeEntry.timeEntryId"
    )
    public void deleteTimeEntry(TimeEntry timeEntry) {
        TimeEntry foundTimeEntry = this.timeEntryRepository.findById(timeEntry.getTimeEntryId())
                .orElseThrow(() -> new ResourceNotFoundException("The provided timeEntryId does not exist"));
        this.timeEntryRepository.delete(foundTimeEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<TimeEntry> getTimeEntriesByProjectCriteria(String projectCriteria) throws ResourceNotFoundException {
        ProjectDTO projectDTO = this.projectServiceClient.getProjectByNameOrId(projectCriteria);
        return this.timeEntryRepository.findByProjectId(projectDTO.getProjectId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<TimeEntry> getTimeEntriesByTaskName(String taskName) throws ResourceNotFoundException {
        TaskDTO taskDTO = this.taskServiceClient.getTaskByName(taskName);
        return this.timeEntryRepository.findByTaskId(taskDTO.getTaskId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntry> getTimeEntriesForLastMonth() {
        // 'start' represents the start of the first day of the previous month
        LocalDateTime start = LocalDateTime.now()
                .minusMonths(1) // Get the same day of the previous month
                .withDayOfMonth(1) // Set the day of the month to 1 to get the first day of the previous month
                .withHour(0).withMinute(0).withSecond(0); // Set the time to 00:00:00 to get the start of the day

        // 'end' represents the end of the last day of the previous month
        LocalDateTime end = start.plusMonths(1) // Get the first day of the current month
                .minusSeconds(1); // Get the last moment of the last day of the previous month

        return this.timeEntryRepository.findTimeEntriesWithinRange(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntry> getTimeEntriesForLastDay() {
        // 'start' represents yesterday
        LocalDateTime start = LocalDateTime.now()
                .minusDays(1) // Get yesterday
                .withHour(0).withMinute(0).withSecond(0); // Set the time to 00:00:00 to get the start of the day

        // 'end' represents end of yesterday
        LocalDateTime end = start.plusDays(1) // Get today
                .minusSeconds(1); // Get the last moment of today

        return this.timeEntryRepository.findTimeEntriesWithinRange(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntry> getTimeEntriesForLastWeek() {
        // TODO: start part is AI Generated. I'm confused asf myself :)
        // 'start' represents the start of the Saturday of the previous week
        LocalDateTime start = LocalDateTime.now()
                .minusWeeks(1) // Get the same day of the previous week
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)) // Adjust to the next or same Saturday
                .minusWeeks(1) // Then go to the previous week
                .withHour(0).withMinute(0).withSecond(0); // Set the time to 00:00:00 to get the start of the day

        // 'end' represents the end of the Friday of the previous week
        LocalDateTime end = start.plusWeeks(1) // Add one week to the start date to get the Saturday of the current week
                .minusSeconds(1); // Subtract one second to get the last moment of the Friday of the previous week

        return this.timeEntryRepository.findTimeEntriesWithinRange(start, end);
    }

}
