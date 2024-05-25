package com.seyed.ali.timeentryservice.client;

import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * This is the base service client class that handles the common logic for sending HTTP requests.
 * <p>
 * It uses Spring's {@link WebClient} to send requests and {@link KeycloakSecurityUtil} to handle JWT tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceClient {

    private final KeycloakSecurityUtil keycloakSecurityUtil;
    private final WebClient.Builder webClientBuilder;
    protected WebClient webClient;

    /**
     * Initializes the WebClient instance used for sending HTTP requests.
     */
    @PostConstruct
    public void init() {
        this.webClient = this.webClientBuilder.build();
    }

    /**
     * Sends an HTTP request and returns the response body.
     *
     * @param url          the URL to send the request to
     * @param httpMethod   the HTTP method (GET, POST, etc.)
     * @param responseType the type of the response body. ParameterizedTypeReference is used to handle different types of responses from different services.
     *                     Some services return a {@link Result} object that contains a data field of a specific type, while others return a {@link String}, etc...
     *                     {@link ParameterizedTypeReference} allows you to specify the type of the data field at runtime.
     * @param <T>          the type of the response body
     * @return the response body
     */
    public <T> T sendRequest(String url, HttpMethod httpMethod, ParameterizedTypeReference<T> responseType) {
        String jwt = this.keycloakSecurityUtil.extractTokenFromSecurityContext();  // Get the JWT token

        // Send the HTTP request and return the response body
        return this.webClient
                .method(httpMethod).uri(url)  // Set the HTTP method and URL
                .header("Authorization", "Bearer " + jwt)  // Add the Authorization header
                .retrieve()  // Retrieve the response
                .onStatus(NOT_FOUND::equals,
                        resp -> Mono.error(new ResourceNotFoundException("Requested Resource Was Not Found!"))
                )  // Handle NOT_FOUND status
                .bodyToMono(responseType)  // Convert the response body to the specified type
                .block();  // Block until the response is received
    }

}
