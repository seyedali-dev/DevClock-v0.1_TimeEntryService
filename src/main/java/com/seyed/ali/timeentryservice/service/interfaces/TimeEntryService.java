package com.seyed.ali.timeentryservice.service.interfaces;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;

import java.util.List;

public interface TimeEntryService {

    List<TimeEntryDTO> getTimeEntries();

    TimeEntryDTO getUsersTimeEntry(String userId);

    String addTimeEntryManually(TimeEntryDTO timeEntryDTODTO);

}
