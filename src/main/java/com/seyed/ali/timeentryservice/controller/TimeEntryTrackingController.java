package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.payload.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time/track")
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Track TimeEntry", description = "API for tracking time - timer mode.")
public class TimeEntryTrackingController {

    private final TimeEntryTrackingService timeEntryService;

    @PostMapping("/start")
    @Operation(summary = "Start Tracking Time", description = "Starts tracking time - timer.")
    public ResponseEntity<Result> startTrackingTimeEntry(@Valid @RequestBody TimeBillingDTO timeBillingDTO) {
        return ResponseEntity.status(CREATED).body(new Result(
                true,
                CREATED,
                "Time tracking started...",
                this.timeEntryService.startTrackingTimeEntry(timeBillingDTO)
        ));
    }

    @PutMapping("/stop/{timeEntryId}")
    @Operation(summary = "Stop Tracking Time", description = "Stops tracking time - timer.")
    public ResponseEntity<Result> stopTrackingTimeEntry(@PathVariable String timeEntryId) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Time tracking stopped.",
                this.timeEntryService.stopTrackingTimeEntry(timeEntryId)
        ));
    }

    @PutMapping("/continue/{timeEntryId}")
    @Operation(summary = "Continue Tracking Time", description = "Continues tracking time - timer.")
    public ResponseEntity<Result> continueTrackingTimeEntry(@PathVariable String timeEntryId) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Ok 👌🏻. Time tracking continued...",
                this.timeEntryService.continueTrackingTimeEntry(timeEntryId)
        ));
    }

}
