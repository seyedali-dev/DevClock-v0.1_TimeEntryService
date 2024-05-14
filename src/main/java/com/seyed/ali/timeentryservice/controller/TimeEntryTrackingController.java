package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.dto.response.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    public Result startTrackingTimeEntry(@RequestBody TimeBillingDTO timeBillingDTO) {
        boolean billable = timeBillingDTO.isBillable();
        BigDecimal hourlyRate = timeBillingDTO.getHourlyRate();
        return new Result(
                true,
                CREATED,
                "Time tracking started...",
                this.timeEntryService.startTrackingTimeEntry(billable, hourlyRate)
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
