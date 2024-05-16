package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.dto.response.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryTrackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Result> startTrackingTimeEntry(@Valid @RequestBody TimeBillingDTO timeBillingDTO) {
        boolean billable = false;
        BigDecimal hourlyRate = BigDecimal.ZERO;

        if (timeBillingDTO != null) {
            billable = timeBillingDTO.billable();
            hourlyRate = timeBillingDTO.hourlyRate();
        }
        return ResponseEntity.status(CREATED).body(new Result(
                true,
                CREATED,
                "Time tracking started...",
                this.timeEntryService.startTrackingTimeEntry(billable, hourlyRate)
        ));
    }

    @PutMapping("/stop/{timeEntryId}")
    public ResponseEntity<Result> stopTrackingTimeEntry(@PathVariable String timeEntryId) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Time tracking stopped.",
                this.timeEntryService.stopTrackingTimeEntry(timeEntryId)
        ));
    }

    @PutMapping("/continue/{timeEntryId}")
    public ResponseEntity<Result> continueTrackingTimeEntry(@PathVariable String timeEntryId) {
        return ResponseEntity.ok(new Result(
                true,
                OK,
                "Ok 👌🏻. Time tracking continued...",
                this.timeEntryService.continueTrackingTimeEntry(timeEntryId)
        ));
    }

}
