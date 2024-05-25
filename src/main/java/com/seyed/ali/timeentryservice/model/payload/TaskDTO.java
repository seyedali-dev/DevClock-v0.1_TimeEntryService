package com.seyed.ali.timeentryservice.model.payload;

import com.seyed.ali.timeentryservice.model.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.seyed.ali.TaskService.model.domain.Task}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO implements Serializable {

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the task", example = "12345")
    private String taskId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The task name", example = "Learn Security in Microservices")
    private String taskName;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The task description", example = "We can use keycloak for ease of use in microservice security!")
    private String taskDescription;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The status of task", example = "COMPLETED | IN_PROGRESS | DROPPED | PLANNED_FOR_FUTURE")
    private TaskStatus taskStatus;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The order of the task", example = "1")
    private Integer taskOrder;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique identifier for the project that the task needs to be associated with", example = "12345")
    private String projectId;

}