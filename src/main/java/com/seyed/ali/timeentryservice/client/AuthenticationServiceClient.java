package com.seyed.ali.timeentryservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthenticationServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final KeycloakSecurityUtil keycloakSecurityUtil;
    private final ObjectMapper objectMapper;
    private @Value("${authentication.service.user-persistence-controller.base-url}") String authenticationServiceClient_BaseUrl;
    private @Value("${authentication.service.user-persistence-controller.handle-user-url}") String authenticationServiceClient_HandleUserUrl;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = this.webClientBuilder
                .baseUrl(this.authenticationServiceClient_BaseUrl)
                .build();
    }

    // TODO: Cache this method in REDIS
    public String getCurrentLoggedInUsersId() {
        String jwtToken = this.keycloakSecurityUtil.extractTokenFromSecurityContext();
        String userJson = this.webClient
                .post().uri(this.authenticationServiceClient_HandleUserUrl)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        String userId;
        try {
            JsonNode jsonNode = this.objectMapper.readTree(userJson);
            userId = jsonNode.get("id").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

}
