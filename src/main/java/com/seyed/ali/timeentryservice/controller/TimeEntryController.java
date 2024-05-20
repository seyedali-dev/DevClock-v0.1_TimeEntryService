package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.response.Result;
import com.seyed.ali.timeentryservice.model.dto.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.converter.TimeEntryConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final TimeEntryConverter timeEntryConverter;
    private final TimeParser timeParser;

    @GetMapping
    @Operation(summary = "Get all time entries", responses = {
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
    public ResponseEntity<Result> addTimeEntryManually(@Valid @RequestBody TimeEntryDTO timeEntryDTO) {
        return ResponseEntity.status(CREATED).body(new Result(
                true,
                CREATED,
                "Time entry created successfully.",
                this.timeEntryService.addTimeEntryManually(timeEntryDTO)
        ));
    }

    @PutMapping("/{timeEntryId}")
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
    public ResponseEntity<Result> deleteTimeEntry(@PathVariable String timeEntryId) {
        this.timeEntryService.deleteTimeEntry(timeEntryId);
        return ResponseEntity.status(NO_CONTENT).body(new Result(
                true,
                NO_CONTENT,
                "Time entry deleted successfully."
        ));
    }

}
