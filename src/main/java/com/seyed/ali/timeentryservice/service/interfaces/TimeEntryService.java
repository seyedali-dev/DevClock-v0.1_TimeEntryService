package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryResponse;

import java.util.List;

public interface TimeEntryService {

    List<TimeEntryResponse> getTimeEntries();

    TimeEntryResponse getUsersTimeEntry(String userId);

    String addTimeEntryManually(TimeEntryDTO timeEntryDTODTO);

    TimeEntryDTO updateTimeEntryManually(String id, TimeEntryDTO timeEntryDTO);

    void deleteTimeEntry(String id);

}
