package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeParser timeParser;
    private final TimeEntryUtility timeEntryUtility;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntryResponse> getTimeEntries() {
        List<TimeEntry> timeEntryList = this.timeEntryRepository.findAll();
        return this.timeEntryUtility.convertToTimeEntryResponseList(timeEntryList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeEntryResponse getUsersTimeEntry(String userId) {
        TimeEntry timeEntry = this.timeEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        return this.timeEntryUtility.convertToTimeEntryResponse(timeEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryUtility.createTimeEntry(timeEntryDTO);
        this.timeEntryRepository.save(timeEntry);
        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        return this.timeParser.parseTimeToString(lastTimeSegment.getStartTime(), lastTimeSegment.getEndTime(), lastTimeSegment.getDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TimeEntryDTO updateTimeEntryManually(String timeEntryId, TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new IllegalArgumentException("The provided timeEntryId does not exist"));
        this.timeEntryUtility.updateTimeEntry(timeEntry, timeEntryDTO, this.timeParser);
        this.timeEntryRepository.save(timeEntry);
        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        String startTimeString = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getStartTime());
        return this.timeEntryUtility.createTimeEntryDTO(timeEntry, lastTimeSegment, startTimeString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTimeEntry(String timeEntryId) {
        this.timeEntryRepository.deleteById(timeEntryId);
    }

}
