package com.seyed.ali.timeentryservice.service.cache;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TimeEntryCacheManager {

    public static final String TIME_ENTRY_CACHE = "time-entry-cache";

    @SuppressWarnings("unused")
    @CachePut(
            cacheNames = TIME_ENTRY_CACHE,
            key = "#timeEntryId"
    )
    public TimeEntry cacheTimeEntry(String timeEntryId, TimeEntry timeEntry) {
        log.info("Caching timeEntry. TimeEntryId: {} - UserId: {}", timeEntryId, timeEntry.getUserId());
        return timeEntry;
    }

}
