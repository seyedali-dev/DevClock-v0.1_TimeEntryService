package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;

public interface TimeEntryTrackingService {

    // TODO: Implement REDIS for caching the `start_time`
    String startTrackingTimeEntry();

    // TODO: Implement REDIS for getting the cached `start_time`
    TimeEntryDTO stopTrackingTimeEntry(String timeEntryId);

    TimeEntryDTO continueTrackingTimeEntry(String timeEntryId);

}
