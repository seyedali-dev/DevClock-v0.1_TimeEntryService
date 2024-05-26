package com.seyed.ali.timeentryservice.client;

import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This is the service client class for the task service responsible for making REST calls to {@code Task-Service}.
 * <p>
 * It extends the base ServiceClient class and adds specific logic for handling task requests.
 */
@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@Component
public class TaskServiceClient extends ServiceClient {

    private final String taskServiceBaseURL = "http://localhost:8084/api/v1/task/client"; // TODO: remember to change the host and port when dockerizing the application

    public TaskServiceClient(KeycloakSecurityUtil keycloakSecurityUtil, WebClient.Builder webClientBuilder) {
        super(keycloakSecurityUtil, webClientBuilder);
    }

    /**
     * Checks if a task is valid.
     *
     * @param taskId the ID of the task
     * @return true if the task is valid, false otherwise
     */
    public boolean isTaskValid(String taskId) {
        String url = this.taskServiceBaseURL + "/" + taskId;
        Result booleanResult = this.sendRequest(url, HttpMethod.GET, new ParameterizedTypeReference<>() {
        });
        return (boolean) booleanResult.getData();
    }

}
