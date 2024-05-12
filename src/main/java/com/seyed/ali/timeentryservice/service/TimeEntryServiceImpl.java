package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final TimeParser timeParser;

    @Override
    public List<TimeEntryDTO> getTimeEntries() {
        return this.timeEntryRepository.findAll()
                .stream()
                .map(timeEntry -> {
                    String startTimeString = this.timeParser.parseLocalDateTimeToString(timeEntry.getStartTime());
                    String endTimeString = this.timeParser.parseLocalDateTimeToString(timeEntry.getEndTime());
                    String durationString = this.timeParser.parseDurationToString(timeEntry.getDuration());
                    return new TimeEntryDTO(timeEntry.getId(), startTimeString, endTimeString, durationString);
                }).toList();
    }

    @Override
    public TimeEntryDTO getUsersTimeEntry(String userId) {
        TimeEntry timeEntry = this.timeEntryRepository.findByUserId(userId);
        String startTime = this.timeParser.parseLocalDateTimeToString(timeEntry.getStartTime());
        String endTime = this.timeParser.parseLocalDateTimeToString(timeEntry.getEndTime());
        String duration = this.timeParser.parseDurationToString(timeEntry.getDuration());
        return new TimeEntryDTO(timeEntry.getId(), startTime, endTime, duration);
    }

    @Override
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = createTimeEntry(timeEntryDTO);
        this.timeEntryRepository.save(timeEntry);
        return this.timeParser.parseTimeToString(timeEntry.getStartTime(), timeEntry.getEndTime(), timeEntry.getDuration());
    }

    // TODO
    private TimeEntry createTimeEntry(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(UUID.randomUUID().toString());

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

        timeEntry.setStartTime(startTime);
        timeEntry.setEndTime(endTime);
        timeEntry.setDuration(calculatedDuration);
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());

        return timeEntry;
    }

    // TODO: Implement REDIS for caching the `start_time`
    @Override
    public void startTrackingTimeEntry() {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(UUID.randomUUID().toString());
        timeEntry.setStartTime(LocalDateTime.now());
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId());
        this.timeEntryRepository.save(timeEntry);
    }

    // TODO: Implement REDIS for getting the cached `start_time`
    @Override
    public TimeEntryDTO stopTrackingTimeEntry() {
        LocalDateTime endTime = LocalDateTime.now();

        String currentLoggedInUsersId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        TimeEntry timeEntry = this.timeEntryRepository.findByUserId(currentLoggedInUsersId);
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

    @Override
    public TimeEntryDTO updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO) {
        Optional<TimeEntry> timeEntryOptional = this.timeEntryRepository.findById(id);

        if (timeEntryOptional.isPresent()) {
            TimeEntry timeEntry = timeEntryOptional.get();

            LocalDateTime startTime = timeEntry.getStartTime();
            LocalDateTime endTime = timeEntry.getEndTime();

            if (timeEntryDTO.startTime() != null)
                startTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.startTime());
            if (timeEntryDTO.endTime() != null)
                endTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.endTime());
            Duration calculatedDuration = Duration.between(startTime, endTime);

            timeEntry.setStartTime(startTime);
            timeEntry.setEndTime(endTime);
            timeEntry.setDuration(calculatedDuration);

            this.timeEntryRepository.save(timeEntry);

            String startTimeString = this.timeParser.parseLocalDateTimeToString(timeEntry.getStartTime());
            String endTimeString = this.timeParser.parseLocalDateTimeToString(timeEntry.getEndTime());
            String durationString = this.timeParser.parseDurationToString(timeEntry.getDuration());

            return new TimeEntryDTO(timeEntry.getId(), startTimeString, endTimeString, durationString);
        } else throw new IllegalArgumentException("The provided id does not exist");
    }

    @Override
    public void deleteTimeEntry(String id) {
        this.timeEntryRepository.deleteById(id);
    }

}
