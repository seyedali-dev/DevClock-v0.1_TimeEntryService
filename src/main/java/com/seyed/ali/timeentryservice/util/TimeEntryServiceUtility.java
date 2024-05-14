package com.seyed.ali.timeentryservice.util;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class TimeEntryServiceUtility {

    private final TimeSegmentRepository timeSegmentRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final TimeParser timeParser;

    public TimeEntry createTimeEntry(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimeEntryId(UUID.randomUUID().toString());

        LocalDateTime startTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.startTime());
        LocalDateTime endTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.endTime());
        Duration calculatedDuration = Duration.between(startTime, endTime);

        // if the user entered `duration` field
        Optional<Duration> durationOpt = Optional.ofNullable(timeEntryDTO.duration())
                .map(this.timeParser::parseStringToDuration);

        durationOpt.ifPresent(duration -> {
            if (!calculatedDuration.equals(duration)) {
                throw new IllegalArgumentException("The provided endTime and duration are not consistent with the startTime");
            }
        });

        TimeSegment timeSegment = new TimeSegment();
        timeSegment.setStartTime(startTime);
        timeSegment.setEndTime(endTime);
        timeSegment.setDuration(calculatedDuration);
        this.timeSegmentRepository.save(timeSegment);

        timeEntry.getTimeSegmentList().add(timeSegment);
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());

        return timeEntry;
    }

}
