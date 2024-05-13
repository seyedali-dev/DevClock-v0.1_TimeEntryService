package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time/track")
@SecurityRequirement(name = "Keycloak")
public class TimeEntryTrackingController {

    private final TimeEntryTrackingService timeEntryService;

    @PostMapping("/start")
    @ResponseStatus(CREATED)
    public Result startTrackingTimeEntry() {
        return new Result(
                true,
                CREATED,
                "Time tracking started...",
                this.timeEntryService.startTrackingTimeEntry()
        );
    }

    @PutMapping("/stop/{timeEntryId}")
    @ResponseStatus(OK)
    public Result stopTrackingTimeEntry(@PathVariable String timeEntryId) {
        return new Result(
                true,
                OK,
                "Time tracking stopped.",
                this.timeEntryService.stopTrackingTimeEntry(timeEntryId)
        );
    }

    @PutMapping("/continue/{timeEntryId}")
    @ResponseStatus(OK)
    public Result continueTrackingTimeEntry(@PathVariable String timeEntryId) {
        return new Result(
                true,
                OK,
                "Ok 👌🏻. Time tracking continued...",
                this.timeEntryService.continueTrackingTimeEntry(timeEntryId)
        );
    }

}
