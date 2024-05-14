package com.seyed.ali.timeentryservice.util;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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
        boolean billable = timeEntryDTO.billable();
        String hourlyRate = timeEntryDTO.hourlyRate() != null ? timeEntryDTO.hourlyRate() : null;

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimeEntryId(UUID.randomUUID().toString());
        timeEntry.setBillable(billable);
        if (hourlyRate != null)
            timeEntry.setHourlyRate(new BigDecimal(hourlyRate));

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

        this.createTimeInfo(timeEntry, startTime, endTime, calculatedDuration);
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());

        return timeEntry;
    }

    public void createTimeInfo(TimeEntry timeEntry, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        TimeSegment timeSegment = new TimeSegment();
        timeSegment.setTimeSegmentId(UUID.randomUUID().toString());
        timeSegment.setStartTime(startTime);
        timeSegment.setEndTime(endTime);
        timeSegment.setDuration(duration);
        timeSegment.setTimeEntry(timeEntry);
        this.timeSegmentRepository.save(timeSegment);

        timeEntry.getTimeSegmentList().add(timeSegment);
    }

    public TimeEntryDTO createTimeEntryDTO(TimeEntry timeEntry, TimeSegment lastTimeSegment, String startTimeString) {
        String endTimeString = lastTimeSegment.getEndTime() != null ? this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getEndTime()) : null;
        String durationString = lastTimeSegment.getDuration() != null ? this.timeParser.parseDurationToString(lastTimeSegment.getDuration()) : null;
        boolean billable = timeEntry.isBillable();
        String hourlyRate = timeEntry.getHourlyRate().toString();

        return new TimeEntryDTO(timeEntry.getTimeEntryId(), startTimeString, endTimeString, billable, hourlyRate, durationString);
    }

    public Duration getTotalDuration(TimeEntry timeEntry) {
        return timeEntry.getTimeSegmentList().stream()
                .map(TimeSegment::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

}
