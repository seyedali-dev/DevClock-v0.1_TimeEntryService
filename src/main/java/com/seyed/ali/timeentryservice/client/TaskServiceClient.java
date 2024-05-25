package com.seyed.ali.timeentryservice.client;

import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.TaskDTO;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * This is the service client class for the task service responsible for making REST calls to {@code Task-Service}.
 * <p>
 * It extends the base ServiceClient class and adds specific logic for handling task requests.
 */
@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@Component
public class TaskServiceClient extends ServiceClient {

    private final String taskServiceBaseURL = "http://localhost:8084/api/v1/task"; // TODO: remember to change the host and port when dockerizing the application

    public TaskServiceClient(KeycloakSecurityUtil keycloakSecurityUtil, WebClient.Builder webClientBuilder) {
        super(keycloakSecurityUtil, webClientBuilder);
    }

    /**
     * Retrieves all tasks for a specific project.
     *
     * @param projectId the ID of the project
     * @return a list of tasks for the project
     */
    public List<TaskDTO> findAllTasksForProject(String projectId) {
        String url = this.taskServiceBaseURL + "/" + projectId;
        Result listResult = this.sendRequest(url, HttpMethod.GET, new ParameterizedTypeReference<>() {
        });
        return (List<TaskDTO>) listResult.getData();
    }

}
