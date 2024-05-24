package com.seyed.ali.timeentryservice.client;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectServiceClient {

    private final String projectServiceBaseURL = "http://localhost:8083/api/v1"; // TODO: remember to change the host and port when dockerizing the application

    private final WebClient webClient;
    private final KeycloakSecurityUtil keycloakSecurityUtil;

    public boolean isProjectValid(String projectId) {
        String jwt = this.keycloakSecurityUtil.extractTokenFromSecurityContext();

        Result result = this.webClient
                .get().uri(this.projectServiceBaseURL + "/project/client/" + projectId)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .onStatus(NOT_FOUND::equals,
                        response -> Mono.error(new ResourceNotFoundException("Project with ProjectID '" + projectId + "' not found"))
                )
                .bodyToMono(Result.class)
                .block();

        assert result != null;
        return (boolean) result.getData();
    }

}