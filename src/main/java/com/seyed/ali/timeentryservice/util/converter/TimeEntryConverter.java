package com.seyed.ali.timeentryservice.util.converter;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.TimeSegmentDTO;
import com.seyed.ali.timeentryservice.model.dto.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// TODO: Write unit tests!
@Service
@RequiredArgsConstructor
public class TimeEntryConverter {

    private final TimeParser timeParser;
    private final TimeEntryUtility timeEntryUtility;

    /**
     * Converts a list of TimeEntry objects to a list of TimeEntryResponse objects.
     *
     * @param timeEntryList The list of TimeEntry objects to convert.
     * @return A list of TimeEntryResponse objects.
     */
    public List<TimeEntryResponse> convertToTimeEntryResponseList(List<TimeEntry> timeEntryList) {
        List<TimeEntryResponse> timeEntryResponseList = new ArrayList<>();
        for (TimeEntry timeEntry : timeEntryList) {
            TimeEntryResponse timeEntryResponse = this.convertToTimeEntryResponse(timeEntry);
            timeEntryResponseList.add(timeEntryResponse);
        }
        return timeEntryResponseList;
    }

    /**
     * Converts a TimeEntry object to a TimeEntryResponse object.
     *
     * @param timeEntry The TimeEntry object to convert.
     * @return A TimeEntryResponse object.
     */
    public TimeEntryResponse convertToTimeEntryResponse(TimeEntry timeEntry) {
        String hourlyRate = timeEntry.getHourlyRate() != null
                ? timeEntry.getHourlyRate().toString()
                : null;
        List<TimeSegmentDTO> timeSegmentDTOList = new ArrayList<>();
        List<TimeSegment> timeSegmentList = timeEntry.getTimeSegmentList();
        Duration totalDuration = this.timeEntryUtility.getTotalDuration(timeEntry);

        for (TimeSegment timeSegment : timeSegmentList) {
            String startTimeStr = this.timeParser.parseLocalDateTimeToString(timeSegment.getStartTime());
            String endTimeStr = timeSegment.getEndTime() != null
                    ? this.timeParser.parseLocalDateTimeToString(timeSegment.getEndTime())
                    : null;
            String durationStr = timeSegment.getDuration() != null
                    ? this.timeParser.parseDurationToString(timeSegment.getDuration())
                    : null;
            TimeSegmentDTO segmentDTO = new TimeSegmentDTO(timeSegment.getTimeSegmentId(), startTimeStr, endTimeStr, durationStr, timeEntry.getUserId());
            timeSegmentDTOList.add(segmentDTO);
        }

        String totalDurationStr = this.timeParser.parseDurationToString(totalDuration);
        return new TimeEntryResponse(timeEntry.getTimeEntryId(), timeSegmentDTOList, timeEntry.isBillable(), hourlyRate, totalDurationStr);
    }

    /**
     * Creates a TimeEntryDTO object based on the provided TimeEntry and TimeSegment objects.
     *
     * @param timeEntry       The TimeEntry object.
     * @param lastTimeSegment The last TimeSegment object associated with the time entry.
     * @param startTimeString The start time string for the time entry.
     * @return The created TimeEntryDTO object.
     */
    public TimeEntryDTO createTimeEntryDTO(TimeEntry timeEntry, TimeSegment lastTimeSegment, String startTimeString) {
        String hourlyRate = timeEntry.getHourlyRate() != null
                ? timeEntry.getHourlyRate().toString()
                : null;
        String endTimeStr = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getEndTime());
        String durationStr = this.timeParser.parseDurationToString(lastTimeSegment.getDuration());
        return new TimeEntryDTO(timeEntry.getTimeEntryId(), startTimeString, endTimeStr, timeEntry.isBillable(), hourlyRate, durationStr, timeEntry.getProjectId());
    }

}
