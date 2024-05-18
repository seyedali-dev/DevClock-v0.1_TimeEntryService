package com.seyed.ali.timeentryservice.service.cache;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class TimeEntryCacheManager {

    public static final String TIME_ENTRY_CACHE = "time-entry-cache";

    @SuppressWarnings("unused")
    @CachePut(
            cacheNames = TIME_ENTRY_CACHE,
            key = "#timeEntryId"
    )
    public TimeEntry cacheTimeEntry(String timeEntryId, TimeEntry timeEntry) {
        return timeEntry;
    }

}
