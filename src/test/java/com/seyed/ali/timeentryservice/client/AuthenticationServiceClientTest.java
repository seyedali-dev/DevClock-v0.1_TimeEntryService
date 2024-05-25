/*
package com.seyed.ali.timeentryservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceClientTest {

    private @InjectMocks AuthenticationServiceClient authenticationServiceClient;
    private @Mock WebClient.Builder webClientBuilder;
    private @Mock WebClient webClient;
    private @Mock KeycloakSecurityUtil keycloakSecurityUtil;
    private @Mock ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        // inject @Value(..) fields
        Field authenticationServiceClient_BaseUrl_Field = AuthenticationServiceClient.class // the service class
                .getDeclaredField("authenticationServiceClient_BaseUrl"); // the @Value field
        authenticationServiceClient_BaseUrl_Field.setAccessible(true);
        authenticationServiceClient_BaseUrl_Field.set(this.authenticationServiceClient, "some_base_url");

        Field authenticationServiceClient_HandleUserUrl_Field = AuthenticationServiceClient.class
                .getDeclaredField("authenticationServiceClient_HandleUserUrl");
        authenticationServiceClient_HandleUserUrl_Field.setAccessible(true);
        authenticationServiceClient_HandleUserUrl_Field.set(this.authenticationServiceClient, "some_handle_user_url");

        // mocking `WebClientBuilder#baseUrl(String)`
        when(this.webClientBuilder.baseUrl(isA(String.class)))
                .thenReturn(this.webClientBuilder);

        // mocking `WebClientBuilder#build()`
        when(this.webClientBuilder.build())
                .thenReturn(this.webClient);

        // mocking `WebClient#post()`
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(this.webClient.post())
                .thenReturn(requestBodyUriSpec);

        // mocking `WebClient#post()`
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        when(requestBodyUriSpec.uri(isA(String.class)))
                .thenReturn(requestBodySpec);

        // mocking `RequestHeadersSpec#header(String, String)`
        when(requestBodySpec.header(eq("Authorization"), isA(String.class)))
                .thenReturn(requestBodySpec);

        // mocking `RequestHeadersSpec#retrieve()`
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

//        // mocking `ResponseSpec#bodyToMono(String)`
//        when(responseSpec.bodyToMono(String.class))
//                .thenReturn(Mono.just("{\"id\":\"123\"}"));

        // mocking `WebClient.ResponseSpec#bodyToMono(ParameterizedTypeReference)`
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<String>() {}))
                .thenReturn(Mono.just("{\"id\":\"123\"}"));


        // The @PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization.In our `AuthenticationServiceClient` class, the `WebClient` instance is being initialized in a `@PostConstruct` annotated method.
        // In the test, Mockito creates a mock of the class before the `@PostConstruct` method is called, so the `WebClient` instance is not initialized,
        // and that’s why we'll get a `NullPointerException`.
        // To solve this, we will manually call the init() method in the `@BeforeEach` annotated method in our test class after the mocks are created.
        this.authenticationServiceClient.init();
    }

    @Test
    public void getCurrentLoggedInUsersIdTest() throws JsonProcessingException {
        // given
        when(this.keycloakSecurityUtil.extractTokenFromSecurityContext())
                .thenReturn("dummyToken");
        when(this.objectMapper.readTree(isA(String.class)))
                .thenReturn(JsonNodeFactory.instance.objectNode().put("id", "123"));

        // when
        String userId = this.authenticationServiceClient.getCurrentLoggedInUsersId();
        System.out.println(userId);

        // then
        assertEquals("123", userId);
    }

}*/
