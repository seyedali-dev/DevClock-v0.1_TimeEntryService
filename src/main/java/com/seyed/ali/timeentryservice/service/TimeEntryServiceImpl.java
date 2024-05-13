package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryServiceUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TimeEntryServiceImpl extends TimeEntryServiceUtility implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeParser timeParser;

    public TimeEntryServiceImpl(
            AuthenticationServiceClient authenticationServiceClient,
            TimeParser timeParser,
            TimeEntryRepository timeEntryRepository
    ) {
        super(authenticationServiceClient, timeParser);
        this.timeEntryRepository = timeEntryRepository;
        this.timeParser = timeParser;
    }

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
