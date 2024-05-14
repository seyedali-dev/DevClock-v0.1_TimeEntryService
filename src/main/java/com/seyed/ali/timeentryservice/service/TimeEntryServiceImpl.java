package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryResponse;
import com.seyed.ali.timeentryservice.model.dto.TimeSegmentDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryServiceUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TimeEntryServiceImpl extends TimeEntryServiceUtility implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeParser timeParser;

    public TimeEntryServiceImpl(TimeSegmentRepository timeSegmentRepository,
                                AuthenticationServiceClient authenticationServiceClient,
                                TimeParser timeParser,
                                TimeEntryRepository timeEntryRepository) {
        super(timeSegmentRepository, authenticationServiceClient, timeParser);
        this.timeEntryRepository = timeEntryRepository;
        this.timeParser = timeParser;
    }

    @Override
    public List<TimeEntryResponse> getTimeEntries() {
        List<TimeEntryResponse> timeEntryResponseList = new ArrayList<>();
        List<TimeSegmentDTO> timeSegmentDTOList = new ArrayList<>();

        List<TimeEntry> timeEntryList = this.timeEntryRepository.findAll();
        for (TimeEntry timeEntry : timeEntryList) {
            List<TimeSegment> timeSegmentList = timeEntry.getTimeSegmentList();
            Duration totalDuration = timeSegmentList.stream()
                    .map(TimeSegment::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
            for (TimeSegment timeSegment : timeSegmentList) {
                LocalDateTime startTime = timeSegment.getStartTime();
                LocalDateTime endTime = timeSegment.getEndTime();
                Duration duration = timeSegment.getDuration();

                String startTimeStr = this.timeParser.parseLocalDateTimeToString(startTime);
                String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
                String durationStr = this.timeParser.parseDurationToString(duration);
                String totalDurationStr = this.timeParser.parseDurationToString(totalDuration);

                TimeSegmentDTO timeSegmentDTO = new TimeSegmentDTO(timeSegment.getTimeSegmentId(), startTimeStr, endTimeStr, durationStr, timeEntry.getTimeEntryId(), timeEntry.getUserId());
                timeSegmentDTOList.add(timeSegmentDTO);

                TimeEntryResponse timeEntryResponse = new TimeEntryResponse(timeEntry.getTimeEntryId(), timeSegmentDTOList, totalDurationStr);
                timeEntryResponseList.add(timeEntryResponse);
            }
        }
        return timeEntryResponseList;
    }

    @Override
    public TimeEntryResponse getUsersTimeEntry(String userId) {
        List<TimeSegmentDTO> timeSegmentDTOList = new ArrayList<>();
        String totalDurationStr = null;

        TimeEntry timeEntry = this.timeEntryRepository.findByUserId(userId);
        List<TimeSegment> timeSegmentList = timeEntry.getTimeSegmentList();
        if (timeSegmentList.isEmpty()) {
            totalDurationStr = this.timeParser.parseDurationToString(timeEntry.getTimeSegmentList().getLast().getDuration());
            return new TimeEntryResponse(timeEntry.getTimeEntryId(), null, totalDurationStr);
        }
        Duration totalDuration = timeSegmentList.stream()
                .map(TimeSegment::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        for (TimeSegment timeSegment : timeSegmentList) {
            LocalDateTime startTime = timeSegment.getStartTime();
            LocalDateTime endTime = timeSegment.getEndTime();
            Duration duration = timeSegment.getDuration();

            String startTimeStr = this.timeParser.parseLocalDateTimeToString(startTime);
            String endTimeStr = this.timeParser.parseLocalDateTimeToString(endTime);
            String durationStr = this.timeParser.parseDurationToString(duration);
            totalDurationStr = this.timeParser.parseDurationToString(totalDuration);

            TimeSegmentDTO timeSegmentDTO = new TimeSegmentDTO(timeSegment.getTimeSegmentId(), startTimeStr, endTimeStr, durationStr, timeEntry.getTimeEntryId(), timeEntry.getUserId());
            timeSegmentDTOList.add(timeSegmentDTO);
        }

        return new TimeEntryResponse(timeEntry.getTimeEntryId(), timeSegmentDTOList, totalDurationStr);

    }

    @Override
    @Transactional
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = createTimeEntry(timeEntryDTO);
        this.timeEntryRepository.save(timeEntry);

        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        return this.timeParser.parseTimeToString(lastTimeSegment.getStartTime(), lastTimeSegment.getEndTime(), lastTimeSegment.getDuration());
    }

    @Override
    @Transactional
    public TimeEntryDTO updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO) {
        Optional<TimeEntry> timeEntryOptional = this.timeEntryRepository.findById(id);

//        if (timeEntryOptional.isPresent()) {
//            TimeEntry timeEntry = timeEntryOptional.get();
//            TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
//
//            LocalDateTime startTime = lastTimeSegment.getStartTime();
//            LocalDateTime endTime = lastTimeSegment.getEndTime();
//            Duration duration = lastTimeSegment.getDuration();
//
//            if (timeEntryDTO.startTime() != null)
//                startTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.startTime());
//            if (timeEntryDTO.endTime() != null)
//                endTime = this.timeParser.parseStringToLocalDateTime(timeEntryDTO.endTime());
//            if (timeEntryDTO.duration() != null)
//                duration = this.timeParser.parseStringToDuration(timeEntryDTO.duration());
//
//            lastTimeSegment.setStartTime(startTime);
//            lastTimeSegment.setEndTime(endTime);
//            lastTimeSegment.setDuration(duration);
//            this.timeSegmentRepository.save(lastTimeSegment);
//
//            timeEntry.getTimeSegmentList().add(lastTimeSegment);
//            this.timeEntryRepository.save(timeEntry);
//
//            String startTimeString = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getStartTime());
//            return getTimeEntryDTO(timeEntry, lastTimeSegment, startTimeString);
//        } else throw new IllegalArgumentException("The provided id does not exist");
        return null;
    }

    @Override
    @Transactional
    public void deleteTimeEntry(String id) {
        this.timeEntryRepository.deleteById(id);
    }

}
