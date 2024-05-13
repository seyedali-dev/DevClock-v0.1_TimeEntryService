package com.seyed.ali.timeentryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyed.ali.timeentryservice.config.EurekaClientTestConfiguration;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@WebMvcTest(TimeEntryController.class) /* since this is not in integration test, rather controller unit test */
@EnableConfigurationProperties /* to use application.yml-test file */
@ActiveProfiles("test")
@AutoConfigureMockMvc/* calling the api itself */
@ContextConfiguration(classes = {EurekaClientTestConfiguration.class}) /* to call the configuration in the test (for service-registry configs) */
class TimeEntryControllerTest {

    private @MockBean TimeEntryService timeEntryService;
    private @MockBean KeycloakSecurityUtil keycloakSecurityUtil;

    private @Autowired ObjectMapper objectMapper;
    private @Autowired MockMvc mockMvc;

    private final String baseUrl = "/api/v1/time";
    private final List<TimeEntryDTO> timeEntries = new ArrayList<>();

    @BeforeEach
    void setUp() {
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO("1", "2024-05-11 08:00:00", "2024-05-11 10:00:00", "02:00:00");

        this.timeEntries.add(timeEntryDTO);
    }

    @Test
    public void getTimeEntriesTest() throws Exception {
        // Given
        when(this.timeEntryService.getTimeEntries())
                .thenReturn(this.timeEntries);

        String some_authority = "some_authority";

        // When
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(this.baseUrl)
                        .accept(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority(some_authority)))
        );

        // Then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("OK")))
                .andExpect(jsonPath("$.message", is("List of time entries.")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is("1")))
                .andExpect(jsonPath("$.data[0].startTime", is("2024-05-11 08:00:00")))
                .andExpect(jsonPath("$.data[0].endTime", is("2024-05-11 10:00:00")))
                .andExpect(jsonPath("$.data[0].duration", is("02:00:00")));
    }

    @Test
    public void getUsersTimeEntryTest() throws Exception {
        // Given
        String userId = "some_user_id";
        when(this.timeEntryService.getUsersTimeEntry(userId))
                .thenReturn(this.timeEntries.getFirst());

        String some_authority = "some_authority";

        // When
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(this.baseUrl + "/" + userId)
                        .accept(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority(some_authority)))
        );

        // Then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("OK")))
                .andExpect(jsonPath("$.message", is("Time entry for user: 'some_user_id' :")))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.startTime", is("2024-05-11 08:00:00")))
                .andExpect(jsonPath("$.data.endTime", is("2024-05-11 10:00:00")))
                .andExpect(jsonPath("$.data.duration", is("02:00:00")));

    }

    @Test
    public void addTimeEntryManuallyTest() throws Exception {
        // given
        TimeEntryDTO timeEntryDTO = this.timeEntries.getFirst();
        String json = this.objectMapper.writeValueAsString(timeEntryDTO);
        String responseMessage = "startTime(" + timeEntryDTO.startTime() + ") " +
                " | endTime (" + timeEntryDTO.endTime() + ")" +
                " | duration(" + timeEntryDTO.duration() + ")";

        when(this.timeEntryService.addTimeEntryManually(timeEntryDTO))
                .thenReturn(responseMessage);

        String someAuthority = "some_authority";

        // when
        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl)
                        .accept(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority(someAuthority)))
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        // then
        response
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("CREATED")))
                .andExpect(jsonPath("$.message", is("Time entry created successfully.")))
                .andExpect(jsonPath("$.data", is("startTime(2024-05-11 08:00:00)  | endTime (2024-05-11 10:00:00) | duration(02:00:00)")));
    }

    @Test
    public void updateTimeEntryTest() throws Exception {
        // Given
        String id = "1";
        TimeEntryDTO timeEntryDTO = this.timeEntries.getFirst();
        String json = this.objectMapper.writeValueAsString(timeEntryDTO);

        when(this.timeEntryService.updateTimeEntryManually(id, timeEntryDTO))
                .thenReturn(timeEntryDTO);

        String someAuthority = "some_authority";

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/" + id)
                        .accept(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority(someAuthority)))
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("OK")))
                .andExpect(jsonPath("$.message", is("Time entry for user: -> " + id + " <- updated successfully.")))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.startTime", is("2024-05-11 08:00:00")))
                .andExpect(jsonPath("$.data.endTime", is("2024-05-11 10:00:00")))
                .andExpect(jsonPath("$.data.duration", is("02:00:00")));
    }

    @Test
    public void deleteTimeEntryTest() throws Exception {
        // Given
        String id = "1";
        doNothing()
                .when(this.timeEntryService)
                .deleteTimeEntry(id);

        String someAuthority = "some_authority";

        // When
        ResultActions response = this.mockMvc.perform(
                MockMvcRequestBuilders.delete(this.baseUrl + "/" + id)
                        .accept(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority(someAuthority)))
        );

        // Then
        response
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("NO_CONTENT")))
                .andExpect(jsonPath("$.message", is("Time entry deleted successfully.")));
    }

}