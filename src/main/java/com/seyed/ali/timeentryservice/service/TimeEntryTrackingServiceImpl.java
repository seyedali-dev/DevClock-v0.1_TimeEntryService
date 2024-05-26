package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.exceptions.OperationNotSupportedException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.payload.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.cache.TimeEntryCacheManager;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.converter.TimeEntryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryTrackingServiceImpl implements TimeEntryTrackingService {

    private final TimeEntryRepository timeEntryRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final TimeParser timeParser;
    private final TimeEntryUtility timeEntryUtility;
    private final TimeEntryCacheManager timeEntryCacheManager;
    private final TimeEntryConverter timeEntryConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String startTrackingTimeEntry(TimeBillingDTO timeBillingDTO) {
        TimeEntry timeEntry = this.timeEntryUtility.createNewTimeEntry(
                this.timeEntryConverter.createTimeBillingDTOFromRequest(timeBillingDTO),
                this.authenticationServiceClient
        );
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        String timeEntryId = timeEntry.getTimeEntryId();
        this.timeEntryCacheManager.cacheTimeEntry(timeEntryId, savedTimeEntry);

        return timeEntryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TimeEntryDTO stopTrackingTimeEntry(String timeEntryId) {
        LocalDateTime endTime = LocalDateTime.now();

        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);

        // Check if the time entry has any time segments and if the last segment has a start time
        if (timeEntry.getTimeSegmentList().isEmpty() || timeEntry.getTimeSegmentList().getLast().getStartTime() == null) {
            // Handle the case where the start time is not set
            throw new OperationNotSupportedException("Cannot stop time entry as the start time is not set.");
        }

        this.timeEntryUtility.stopTimeEntry(timeEntry, endTime);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(timeEntry.getTimeEntryId(), savedTimeEntry);

        Duration totalDuration = this.timeEntryUtility.getTotalDuration(timeEntry);
        String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeEntry.getTimeSegmentList().getLast().getStartTime());
        String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
        String durationStr = this.timeParser.parseDurationToString(totalDuration);

        return new TimeEntryDTO(null, startTimeStr, endTimeStr, timeEntry.isBillable(), timeEntry.getHourlyRate().toString(), durationStr, timeEntry.getProjectId(), timeEntry.getTaskId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TimeEntryDTO continueTrackingTimeEntry(String timeEntryId) {
        LocalDateTime continueTime = LocalDateTime.now();
        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);
        this.timeEntryUtility.continueTimeEntry(timeEntry, continueTime);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(timeEntry.getTimeEntryId(), savedTimeEntry);

        String hourlyRate = timeEntry.getHourlyRate() != null ? timeEntry.getHourlyRate().toString() : null;
        String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeEntry.getTimeSegmentList().getLast().getStartTime());
        return new TimeEntryDTO(timeEntryId, startTimeStr, null, timeEntry.isBillable(), hourlyRate, null, timeEntry.getProjectId(), timeEntry.getTaskId());
    }

}
