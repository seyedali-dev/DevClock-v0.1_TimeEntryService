package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.Result;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    public Result getTimeEntries() {
        return new Result(
                true,
                OK,
                "List of time entries.",
                this.timeEntryService.getTimeEntries()
        );
    }

    @GetMapping("/{userId}")
    public Result getUsersTimeEntry(@PathVariable String userId) {
        return new Result(
                true,
                OK,
                "Time entry for user: '" + userId + "' :",
                this.timeEntryService.getUsersTimeEntry(userId)
        );
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Result addTimeEntryManually(@RequestBody TimeEntryDTO timeEntryDTO) {
        return new Result(
                true,
                CREATED,
                "Time entry created successfully.",
                this.timeEntryService.addTimeEntryManually(timeEntryDTO)
        );
    }

    @PostMapping("/track/start")
    @ResponseStatus(CREATED)
    public Result startTrackingTimeEntry() {
        this.timeEntryService.startTrackingTimeEntry();
        return new Result(
                true,
                CREATED,
                "Time tracking started..."
        );
    }

    @PutMapping("/track/stop")
    @ResponseStatus(OK)
    public Result stopTrackingTimeEntry() {
        return new Result(
                true,
                OK,
                "Time tracking stopped.",
                this.timeEntryService.stopTrackingTimeEntry()
        );
    }

    @PutMapping("/{timeEntryId}")
    @ResponseStatus(OK)
    public Result updateTimeEntryManually(@PathVariable String timeEntryId, @RequestBody TimeEntryDTO timeEntryDTO) {
        return new Result(
                true,
                OK,
                "Time entry for user: -> " + timeEntryId + " <- updated successfully.",
                this.timeEntryService.updateTimeEntryManually(timeEntryId, timeEntryDTO)
        );
    }

    @DeleteMapping("/{timeEntryId}")
    @ResponseStatus(NO_CONTENT)
    public Result deleteTimeEntry(@PathVariable String timeEntryId) {
        this.timeEntryService.deleteTimeEntry(timeEntryId);
        return new Result(
                true,
                NO_CONTENT,
                "Time entry deleted successfully.",
                null
        );
    }

}
