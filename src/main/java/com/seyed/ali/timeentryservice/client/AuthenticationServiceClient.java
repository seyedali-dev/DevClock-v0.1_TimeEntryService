package com.seyed.ali.timeentryservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyed.ali.timeentryservice.exceptions.OperationNotSupportedException;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This is the service client class for the authentication service responsible for making REST calls to {@code Authentication-Service}.
 * <p>
 * It extends the base {@link ServiceClient} class and adds specific logic for handling authentication requests.
 */
@Slf4j
@Component
public class AuthenticationServiceClient extends ServiceClient {

    private @Value("${authentication.service.user-persistence-controller.base-url}") String authenticationServiceClient_BaseUrl;
    private @Value("${authentication.service.user-persistence-controller.handle-user-url}") String authenticationServiceClient_HandleUserUrl;

    private final ObjectMapper objectMapper;

    public AuthenticationServiceClient(KeycloakSecurityUtil keycloakSecurityUtil, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        super(keycloakSecurityUtil, webClientBuilder);
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return the user ID
     */
    public String getCurrentLoggedInUsersId() {
        String url = this.authenticationServiceClient_BaseUrl + this.authenticationServiceClient_HandleUserUrl;
        String userJson = this.sendRequest(url, HttpMethod.POST, new ParameterizedTypeReference<>() {
        });

        if (userJson == null) {
            log.error("Received null response body from {}", url);
            throw new OperationNotSupportedException("Received null response body from AuthenticationServiceClient#getCurrentLoggedInUsersId() - " + url);
        }

        try {

            JsonNode jsonNode = this.objectMapper.readTree(userJson);
            return jsonNode.get("id").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
