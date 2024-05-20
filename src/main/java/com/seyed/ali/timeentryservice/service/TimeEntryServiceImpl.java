package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.cache.TimeEntryCacheManager;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final TimeEntryCacheManager timeEntryCacheManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeEntry> getTimeEntries() {
     return this.timeEntryRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#userId",
            unless = "#result == null"
    )
    public TimeEntry getUsersTimeEntry(String userId) {
        return this.timeEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    @Cacheable(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#timeEntryId",
            unless = "#result == null"
    )
    public TimeEntry getTimeEntryById(String timeEntryId) {
        log.info("Db call.");
        TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                .orElseThrow(()-> new ResourceNotFoundException("Time entry with ID: '" + timeEntryId +"' was not found."));
        timeEntry.getTimeSegmentList().size(); // This will initialize the timeSegmentList: otherwise we'll get hibernate's LazyLoadingException.
        return timeEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String addTimeEntryManually(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryUtility.createTimeEntry(timeEntryDTO);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(savedTimeEntry.getTimeEntryId(), savedTimeEntry);

        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        return this.timeParser.parseTimeToString(lastTimeSegment.getStartTime(), lastTimeSegment.getEndTime(), lastTimeSegment.getDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TimeEntry updateTimeEntryManually(String timeEntryId, TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("The provided timeEntryId does not exist"));
        this.timeEntryUtility.updateTimeEntry(timeEntry, timeEntryDTO, this.timeParser);
        TimeEntry savedTimeEntry = this.timeEntryRepository.save(timeEntry);

        // cache the saved `TimeEntry` to redis
        this.timeEntryCacheManager.cacheTimeEntry(timeEntryId, savedTimeEntry);
        return savedTimeEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(
            cacheNames = TimeEntryCacheManager.TIME_ENTRY_CACHE,
            key = "#timeEntryId"
    )
    public void deleteTimeEntry(String timeEntryId) {
        this.timeEntryRepository.deleteById(timeEntryId);
    }

}
