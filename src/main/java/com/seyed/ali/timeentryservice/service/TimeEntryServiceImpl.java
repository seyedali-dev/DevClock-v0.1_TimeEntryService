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

    // myself:
    // 1. Single Responsibility Principle (SRP): Each method should do one thing and do it well. The addTimeEntryManually method is currently doing several things: parsing the input, validating the input, creating a TimeEntry, and saving it. You could break this method down into smaller methods, each with a single responsibility.
    // 2. Don’t Repeat Yourself (DRY): The parsing of startTime and endTime is repeated. You could create a method to handle this.
    // 3. Use Optional: Instead of checking for null, you could use Optional to handle the possibility of duration being null.
    // 4. Exception Handling: Add proper exception handling for potential parsing errors.
    /*
    @Override
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(UUID.randomUUID().toString());

        LocalDateTime startTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.startTime());
        LocalDateTime endTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.endTime());
        Duration duration = null;
        if (Objects.nonNull(timeEntryDTO.duration())) {
            duration = this.timeParser.parseStringToDuration(timeEntryDTO.duration());
        }
        Duration calculatedDuration = Duration.between(startTime, endTime);
        timeEntry.setDuration(calculatedDuration);
        if (Objects.nonNull(duration) && !calculatedDuration.equals(duration)) {
            throw new IllegalArgumentException("The provided endTime and duration are not consistent with the startTime");
        }

        timeEntry.setStartTime(startTime);
        timeEntry.setEndTime(endTime);
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId()); // TODO
        this.timeEntryRepository.save(timeEntry);

        return this.timeParser.parseTimeToString(timeEntry.getStartTime(), calculatedDuration);
    }
    */

    // bing
    @Override
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = createTimeEntry(timeEntryDTO);
        this.timeEntryRepository.save(timeEntry);
        return this.timeParser.parseTimeToString(timeEntry.getStartTime(), timeEntry.getEndTime(), timeEntry.getDuration());
    }

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
        timeEntry.setUserId(this.authenticationServiceClient.getCurrentLoggedInUsersId()); // TODO

        return timeEntry;
    }

}
