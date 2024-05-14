package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryServiceUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class TimeEntryServiceImpl extends TimeEntryServiceUtility implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeSegmentRepository timeSegmentRepository;
    private final TimeParser timeParser;

    public TimeEntryServiceImpl(
            TimeSegmentRepository timeSegmentRepository,
            AuthenticationServiceClient authenticationServiceClient,
            TimeParser timeParser,
            TimeEntryRepository timeEntryRepository
    ) {
        super(timeSegmentRepository, authenticationServiceClient, timeParser);
        this.timeEntryRepository = timeEntryRepository;
        this.timeSegmentRepository = timeSegmentRepository;
        this.timeParser = timeParser;
    }

    @Override
    public List<TimeEntryDTO> getTimeEntries() {
        AtomicReference<String> timeEntryId = new AtomicReference<>();
        return this.timeEntryRepository.findAll()
                .stream()
                .flatMap(timeEntry -> {
                    timeEntryId.set(timeEntry.getTimeEntryId());
                    return timeEntry.getTimeSegmentList().stream();
                })
                .map(timeSegment -> {
                    String startTimeString = this.timeParser.parseLocalDateTimeToString(timeSegment.getStartTime());
                    String endTimeString = this.timeParser.parseLocalDateTimeToString(timeSegment.getEndTime());
                    String durationString = this.timeParser.parseDurationToString(timeSegment.getDuration());
                    return new TimeEntryDTO(timeEntryId.get(), startTimeString, endTimeString, durationString);
                }).toList();
    }

    @Override
    public TimeEntryDTO getUsersTimeEntry(String userId) {
        TimeEntry timeEntry = this.timeEntryRepository.findByUserId(userId);
        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        String startTime = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getStartTime());
        String endTime = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getEndTime());
        String duration = this.timeParser.parseDurationToString(lastTimeSegment.getDuration());
        return new TimeEntryDTO(timeEntry.getTimeEntryId(), startTime, endTime, duration);
    }

    @Override
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = createTimeEntry(timeEntryDTO);
        this.timeEntryRepository.save(timeEntry);
        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        return this.timeParser.parseTimeToString(lastTimeSegment.getStartTime(), lastTimeSegment.getEndTime(), lastTimeSegment.getDuration());
    }

    @Override
    public TimeEntryDTO updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO) {
        Optional<TimeEntry> timeEntryOptional = this.timeEntryRepository.findById(id);

        if (timeEntryOptional.isPresent()) {
            TimeEntry timeEntry = timeEntryOptional.get();
            TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();

            LocalDateTime startTime = lastTimeSegment.getStartTime();
            LocalDateTime endTime = lastTimeSegment.getEndTime();

            if (timeEntryDTO.startTime() != null)
                startTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.startTime());
            if (timeEntryDTO.endTime() != null)
                endTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.endTime());
            Duration calculatedDuration = Duration.between(startTime, endTime);

            lastTimeSegment.setStartTime(startTime);
            lastTimeSegment.setEndTime(endTime);
            lastTimeSegment.setDuration(calculatedDuration);
            this.timeSegmentRepository.save(lastTimeSegment);

            timeEntry.getTimeSegmentList().add(lastTimeSegment);
            this.timeEntryRepository.save(timeEntry);

            String startTimeString = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getStartTime());
            String endTimeString = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getEndTime());
            String durationString = this.timeParser.parseDurationToString(lastTimeSegment.getDuration());

            return new TimeEntryDTO(timeEntry.getTimeEntryId(), startTimeString, endTimeString, durationString);
        } else throw new IllegalArgumentException("The provided id does not exist");
    }

    @Override
    public void deleteTimeEntry(String id) {
        this.timeEntryRepository.deleteById(id);
    }

}
