package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import com.seyed.ali.timeentryservice.util.TimeEntryServiceUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class TimeEntryTrackingServiceImpl extends TimeEntryServiceUtility implements TimeEntryTrackingService {

    private final TimeEntryRepository timeEntryRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final TimeParser timeParser;

    public TimeEntryTrackingServiceImpl(TimeSegmentRepository timeSegmentRepository,
                                        AuthenticationServiceClient authenticationServiceClient,
                                        TimeParser timeParser,
                                        TimeEntryRepository timeEntryRepository) {
        super(timeSegmentRepository, authenticationServiceClient, timeParser);
        this.timeEntryRepository = timeEntryRepository;
        this.authenticationServiceClient = authenticationServiceClient;
        this.timeParser = timeParser;
    }

    // TODO: Implement REDIS for caching the `start_time`
    @Override
    public String startTrackingTimeEntry() {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimeEntryId(UUID.randomUUID().toString());
        TimeSegment timeSegment = TimeSegment.builder()
                .timeSegmentId(UUID.randomUUID().toString())
                .startTime(LocalDateTime.now())
                .endTime(null)
                .duration(Duration.ZERO)
                .timeEntry(timeEntry)
                .build();

        timeEntry.getTimeSegmentList().add(timeSegment);
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());
        this.timeEntryRepository.save(timeEntry);
        return timeEntry.getTimeEntryId();
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    @Override
    public TimeEntryDTO stopTrackingTimeEntry(String timeEntryId) {
        LocalDateTime endTime = LocalDateTime.now();

        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);

        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        LocalDateTime startTime = lastTimeSegment.getStartTime();
        Duration duration = Duration.between(startTime, endTime);

        createTimeEntry(endTime, timeEntry, startTime, duration);
        this.timeEntryRepository.save(timeEntry);

        // Calculate total duration
        Duration totalDuration = this.getTotalDuration(timeEntry);

        String startTimeStr = this.timeParser.parseLocalDateTimeToString(startTime);
        String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
        String durationStr = this.timeParser.parseDurationToString(totalDuration);
        return new TimeEntryDTO(null, startTimeStr, endTimeStr, durationStr);
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    @Override
    public TimeEntryDTO continueTrackingTimeEntry(String timeEntryId) {
        LocalDateTime continueTime = LocalDateTime.now();
        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndTimeEntryId(currentLoggedInUsersId, timeEntryId);

        TimeSegment timeSegment = TimeSegment.builder()
                .timeSegmentId(UUID.randomUUID().toString())
                .startTime(continueTime)
                .endTime(null)
                .duration(Duration.ZERO)
                .timeEntry(timeEntry)
                .build();

        timeEntry.getTimeSegmentList().add(timeSegment);
        this.timeEntryRepository.save(timeEntry);

        String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeSegment.getStartTime());
        return new TimeEntryDTO(timeEntryId, startTimeStr, null, null);
    }

}
