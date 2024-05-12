package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;

import java.util.List;

public interface TimeEntryService {

    List<TimeEntryDTO> getTimeEntries();

    TimeEntryDTO getUsersTimeEntry(String userId);

    String addTimeEntryManually(TimeEntryDTO timeEntryDTODTO);

    TimeEntryDTO updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO);

    void deleteTimeEntry(String id);

    void startTrackingTimeEntry();

    TimeEntryDTO stopTrackingTimeEntry();

}
