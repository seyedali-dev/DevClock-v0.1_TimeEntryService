package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.dto.response.Result;
import com.seyed.ali.timeentryservice.model.dto.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time")
@SecurityRequirement(name = "Keycloak")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    @Operation(summary = "Get all time entries", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimeEntryResponse.class)))
            )
    })
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
                "Time entry deleted successfully."
        );
    }

}
