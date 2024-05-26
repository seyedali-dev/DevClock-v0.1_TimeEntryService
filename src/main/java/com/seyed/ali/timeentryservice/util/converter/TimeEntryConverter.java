package com.seyed.ali.timeentryservice.util.converter;

import com.seyed.ali.timeentryservice.client.ProjectServiceClient;
import com.seyed.ali.timeentryservice.client.TaskServiceClient;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.payload.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeSegmentDTO;
import com.seyed.ali.timeentryservice.model.payload.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

// TODO: Write unit tests!
@Service
@RequiredArgsConstructor
public class TimeEntryConverter {

    private final TimeParser timeParser;
    private final TimeEntryUtility timeEntryUtility;
    private final ProjectServiceClient projectServiceClient;
    private final TaskServiceClient taskServiceClient;

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
        return new TimeEntryResponse(timeEntry.getTimeEntryId(), timeSegmentDTOList, timeEntry.isBillable(), hourlyRate, totalDurationStr, timeEntry.getProjectId(), timeEntry.getTaskId());
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
        return new TimeEntryDTO(timeEntry.getTimeEntryId(), startTimeString, endTimeStr, timeEntry.isBillable(), hourlyRate, durationStr, timeEntry.getProjectId(), timeEntry.getTaskId());
    }

    /**
     * Creates a TimeBillingDTO object based on the request provided.
     *
     * @param timeBillingDTO the TimeBillingDTO object which user provides where user is free to provide data or to not provide anything.
     * @return The created TimeBillingDTO object.
     */
    public TimeBillingDTO createTimeBillingDTOFromRequest(TimeBillingDTO timeBillingDTO) {
        // add default values
        boolean billable = false;
        BigDecimal hourlyRate = BigDecimal.ZERO;
        String projectId = null;
        String taskId = null;

        // if user provided data
        if (timeBillingDTO != null) {
            billable = timeBillingDTO.isBillable();
            hourlyRate = timeBillingDTO.getHourlyRate();

            // if user also provided task and project ID's
            // validate the provided task and project ID's
            projectId = this.validateId(timeBillingDTO.getProjectId(), this.projectServiceClient::isProjectValid);
            taskId = this.validateId(timeBillingDTO.getTaskId(), this.taskServiceClient::isTaskValid);
        }

        return new TimeBillingDTO(billable, hourlyRate, projectId, taskId);
    }

    /**
     * Validates the provided ID using the specified validator.
     *
     * @param id        The ID to be validated. It can be null.
     * @param validator A Consumer functional interface that takes the ID as input and validates it.
     *                  If the ID is invalid, it is expected to throw an exception.
     * @return The same ID that was passed in, allowing this method to be used in a fluent API style.
     * @throws ResourceNotFoundException If the provided ID was not found.
     */
    private String validateId(String id, Consumer<String> validator) {
        Optional.ofNullable(id).ifPresent(validator);
        return id;
    }

}
