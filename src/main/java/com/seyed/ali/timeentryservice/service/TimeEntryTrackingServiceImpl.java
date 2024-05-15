package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    // TODO: Implement REDIS for caching the `start_time`
    /**
     * {@inheritDoc}
     */
    @Override
    public String startTrackingTimeEntry(boolean billable, BigDecimal hourlyRate) {
        TimeEntry timeEntry = this.timeEntryUtility.createNewTimeEntry(billable, hourlyRate, this.authenticationServiceClient);
        this.timeEntryRepository.save(timeEntry);
        return timeEntry.getTimeEntryId();
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeEntryDTO stopTrackingTimeEntry(String timeEntryId) {
        LocalDateTime endTime = LocalDateTime.now();
        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);
        this.timeEntryUtility.stopTimeEntry(timeEntry, endTime, this.timeParser);
        this.timeEntryRepository.save(timeEntry);
        Duration totalDuration = this.timeEntryUtility.getTotalDuration(timeEntry);
        String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeEntry.getTimeSegmentList().getLast().getStartTime());
        String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
        String durationStr = this.timeParser.parseDurationToString(totalDuration);
        return new TimeEntryDTO(null, startTimeStr, endTimeStr, timeEntry.isBillable(), timeEntry.getHourlyRate().toString(), durationStr);
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeEntryDTO continueTrackingTimeEntry(String timeEntryId) {
        LocalDateTime continueTime = LocalDateTime.now();
        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);
        this.timeEntryUtility.continueTimeEntry(timeEntry, continueTime, this.timeParser);
        this.timeEntryRepository.save(timeEntry);
        String hourlyRate = timeEntry.getHourlyRate() != null ? timeEntry.getHourlyRate().toString() : null;
        String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeEntry.getTimeSegmentList().getLast().getStartTime());
        return new TimeEntryDTO(timeEntryId, startTimeStr, null, timeEntry.isBillable(), hourlyRate, null);
    }

}
