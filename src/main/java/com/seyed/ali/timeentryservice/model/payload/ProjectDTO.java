package com.seyed.ali.timeentryservice.model.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link com.seyed.ali.projectservice.model.domain.Project}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO implements Serializable {

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Unique identifier for the project", example = "12345")
        private String projectId;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The project name", example = "Microservices-Springboot")
        private String projectName;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The project name", example = "Learning microservices is really exciting and HARD ;)")
        private String projectDescription;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The task associated with the project", implementation = TaskDTO.class)
        private List<TaskDTO> taskDTO = new ArrayList<>();

        public ProjectDTO(String projectId, String projectName, String projectDescription) {
                this.projectId = projectId;
                this.projectName = projectName;
                this.projectDescription = projectDescription;
                this.taskDTO = new ArrayList<>();
        }

}