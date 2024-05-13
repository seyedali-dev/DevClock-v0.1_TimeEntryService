package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryTrackingServiceImpl implements TimeEntryTrackingService {

    private final TimeEntryRepository timeEntryRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final TimeParser timeParser;

    // TODO: Implement REDIS for caching the `start_time`
    @Override
    public String startTrackingTimeEntry() {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(UUID.randomUUID().toString());
        timeEntry.setStartTime(LocalDateTime.now());
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());
        this.timeEntryRepository.save(timeEntry);
        return timeEntry.getId();
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    @Override
    public TimeEntryDTO stopTrackingTimeEntry(String timeEntryId) {
        LocalDateTime endTime = LocalDateTime.now();

        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserIdAndId(currentLoggedInUsersId, timeEntryId);
        timeEntry.setEndTime(endTime);

        LocalDateTime startTime = timeEntry.getStartTime();
        Duration duration = Duration.between(startTime, endTime);

        timeEntry.setDuration(duration);

        this.timeEntryRepository.save(timeEntry);

        String startTimeStr = this.timeParser.parseLocalDateTimeToString(startTime);
        String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
        String durationStr = this.timeParser.parseDurationToString(timeEntry.getDuration());
        return new TimeEntryDTO(null, startTimeStr, endTimeStr, durationStr);
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    @Override
    public TimeEntryDTO continueTrackingTimeEntry(String timeEntryId) {
        return null;
    }

}
