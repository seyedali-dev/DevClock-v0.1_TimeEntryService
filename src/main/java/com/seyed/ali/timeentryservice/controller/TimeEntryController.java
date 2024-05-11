package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${endpoint.base-url}/time")
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
    public Result addTimeEntryManually(@RequestBody TimeEntryDTO timeEntryDTO) {
        return new Result(
                true,
                CREATED,
                "Time entry created successfully.",
                this.timeEntryService.addTimeEntryManually(timeEntryDTO)
        );
    }

}
