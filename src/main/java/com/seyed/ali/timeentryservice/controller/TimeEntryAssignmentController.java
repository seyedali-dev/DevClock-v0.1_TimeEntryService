package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/time")
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Assign TimeEntry", description = "API for assigning time entries to project or ...")
public class TimeEntryAssignmentController {

    private final TimeEntryAssignmentService timeEntryAssignmentService;

    @PostMapping("/{timeEntryId}/project/{projectId}")
    @Operation(summary = "Assign project", description = "Assigns a valid project to the specified time entry.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public ResponseEntity<Result> assignTimeEntry(@PathVariable String timeEntryId, @PathVariable String projectId) {
        return ResponseEntity.status(ACCEPTED).body(new Result(
                true,
                ACCEPTED,
                "TimeEntry assigned to project.",
                "ProjectID: " + this.timeEntryAssignmentService.assignProject(timeEntryId, projectId)
        ));
    }

}
