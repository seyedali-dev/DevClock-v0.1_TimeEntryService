package com.seyed.ali.timeentryservice.client;

import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This is the service client class for the project service responsible for making REST calls to {@code Project-Service}.
 * <p>
 * It extends the base {@link ServiceClient} class and adds specific logic for handling project requests.
 */
@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@Component
public class ProjectServiceClient extends ServiceClient {

    private final String projectServiceBaseURL = "http://localhost:8083/api/v1/project/client"; // TODO: remember to change the host and port when dockerizing the application

    public ProjectServiceClient(KeycloakSecurityUtil keycloakSecurityUtil, WebClient.Builder webClientBuilder) {
        super(keycloakSecurityUtil, webClientBuilder);
    }

    /**
     * Checks if a project is valid.
     *
     * @param projectId the ID of the project
     * @return true if the project is valid, false otherwise
     */
    public boolean isProjectValid(String projectId) {
        String url = this.projectServiceBaseURL + "/" + projectId;
        Result booleanResult = this.sendRequest(url, HttpMethod.GET, new ParameterizedTypeReference<>() {
        });
        return (boolean) booleanResult.getData();
    }

}