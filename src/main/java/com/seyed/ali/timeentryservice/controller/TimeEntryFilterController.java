package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time/filter")
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Time Entry - Filter", description = "API for time entry filtering operation")
public class TimeEntryFilterController {

    private final TimeEntryFilterService timeEntryFilterService;

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
                this.timeEntryFilterService.getTimeEntriesByProjectCriteria(projectCriteria)
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
                this.timeEntryFilterService.getTimeEntriesByTaskName(taskName)
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
        List<TimeEntry> timeEntriesForLastMonth = this.timeEntryFilterService.getTimeEntriesForLastMonth();
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
        List<TimeEntry> timeEntriesForLastDay = this.timeEntryFilterService.getTimeEntriesForLastDay();
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Date::LastDay__Size=" + timeEntriesForLastDay.size(),
                timeEntriesForLastDay
        ));
    }

    @GetMapping("/date/last-week")
    @Operation(summary = "Get all time entries for the last week", description = "Fetches all time entries from the database for the last week", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesForLastWeek() {
        List<TimeEntry> timeEntriesForLastWeek = this.timeEntryFilterService.getTimeEntriesForLastWeek();
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Date::LastWeek__Size=" + timeEntriesForLastWeek.size(),
                timeEntriesForLastWeek
        ));
    }

    @GetMapping("/date/today")
    @Operation(summary = "Get all time entries for the today", description = "Fetches all time entries from the database for today", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntry.class)))
            )
    })
    public ResponseEntity<Result> getTimeEntriesForToday() {
        List<TimeEntry> timeEntriesForToday = this.timeEntryFilterService.getTimeEntriesForToday();
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "TimeEntries - Date::ToDay__Size=" + timeEntriesForToday.size(),
                timeEntriesForToday
        ));
    }

}
