package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import com.seyed.ali.timeentryservice.model.payload.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.converter.TimeEntryConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time")
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Time Entry", description = "API for time entry operation")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final TimeEntryConverter timeEntryConverter;
    private final TimeParser timeParser;

    @GetMapping
    @Operation(summary = "Get all time entries", description = "Fetches all time entries from the database", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntryResponse.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntries() {
        List<TimeEntryResponse> timeEntryResponseList = this.timeEntryConverter.convertToTimeEntryResponseList(this.timeEntryService.getTimeEntries());

        return ResponseEntity.ok(new Result(
                true,
                OK,
                "List of time entries.",
                timeEntryResponseList
        ));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get a user's time entry", description = "Fetches a specific user's time entry from the database")
    public ResponseEntity<Result> getUsersTimeEntry(@PathVariable String userId) {
        TimeEntryResponse timeEntryResponse = this.timeEntryConverter.convertToTimeEntryResponse(this.timeEntryService.getUsersTimeEntry(userId));

        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Time entry for user: '" + userId + "' :",
                timeEntryResponse
        ));
    }

    @GetMapping("/{timeEntryId}")
    @Operation(summary = "Get a specific time entry", description = "Fetches a specific time entry from the database")
    public ResponseEntity<Result> getSpecificTimeEntry(@PathVariable String timeEntryId) {
        TimeEntryResponse timeEntryResponse = this.timeEntryConverter.convertToTimeEntryResponse(this.timeEntryService.getTimeEntryById(timeEntryId));
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Time entry: '" + timeEntryId + "'.",
                timeEntryResponse
        ));
    }

    @PostMapping
    @Operation(summary = "Add a time entry manually", description = "Adds a new time entry to the database manually")
    public ResponseEntity<Result> addTimeEntryManually(@Valid @RequestBody TimeEntryDTO timeEntryDTO) {
        return ResponseEntity.status(CREATED).body(new Result(
                true,
                CREATED,
                "Time entry created successfully.",
                this.timeEntryService.addTimeEntryManually(timeEntryDTO)
        ));
    }

    @PutMapping("/{timeEntryId}")
    @Operation(summary = "Update a time entry", description = "Updates a specific time entry in the database")
    public ResponseEntity<Result> updateTimeEntryManually(@Valid @PathVariable String timeEntryId, @RequestBody TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = this.timeEntryService.updateTimeEntryManually(timeEntryId, timeEntryDTO);
        TimeSegment lastTimeSegment = timeEntry.getTimeSegmentList().getLast();
        String startTimeString = this.timeParser.parseLocalDateTimeToString(lastTimeSegment.getStartTime());
        TimeEntryDTO timeEntryDTOResponse = this.timeEntryConverter.createTimeEntryDTO(timeEntry, lastTimeSegment, startTimeString);

        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Time entry for user: -> " + timeEntryId + " <- updated successfully.",
                timeEntryDTOResponse
        ));
    }

    @DeleteMapping("/{timeEntryId}")
    @Operation(summary = "Delete a time entry", description = "Deletes a specific time entry from the database")
    public ResponseEntity<Result> deleteTimeEntry(@PathVariable String timeEntryId) {
        this.timeEntryService.deleteTimeEntry(timeEntryId);
        return ResponseEntity.status(NO_CONTENT).body(new Result(
                true,
                NO_CONTENT,
                "Time entry deleted successfully."
        ));
    }

    // ###################################################################################
    @GetMapping("/project/{projectCriteria}")
    @Operation(summary = "Get all time entries by project(ID or Name)", description = "Fetches all time entries from the database based on name or ID of project", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesByProject(@PathVariable String projectCriteria) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Project",
                this.timeEntryService.getTimeEntriesByProjectCriteria(projectCriteria)
        ));
    }

    @GetMapping("/task/{taskName}")
    @Operation(summary = "Get all time entries by task(name)", description = "Fetches all time entries from the database based on name of Task", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesByTask(@PathVariable String taskName) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Project",
                this.timeEntryService.getTimeEntriesByTaskName(taskName)
        ));
    }

    @GetMapping("/date/last-month")
    @Operation(summary = "Get all time entries for the last month", description = "Fetches all time entries from the database for the last month", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesForLastMonth() {
        List<TimeEntry> timeEntriesForLastMonth = this.timeEntryService.getTimeEntriesForLastMonth();
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Date::LastMonth__Size=" + timeEntriesForLastMonth.size(),
                timeEntriesForLastMonth
        ));
    }

    @GetMapping("/date/last-day")
    @Operation(summary = "Get all time entries for the last day", description = "Fetches all time entries from the database for the last day", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesForLastDay() {
        List<TimeEntry> timeEntriesForLastDay = this.timeEntryService.getTimeEntriesForLastDay();
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Date::LastDay__Size=" + timeEntriesForLastDay.size(),
                timeEntriesForLastDay
        ));
    }

}
