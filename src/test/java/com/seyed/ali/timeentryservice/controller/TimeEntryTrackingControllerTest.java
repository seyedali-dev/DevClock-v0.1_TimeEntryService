package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.config.EurekaClientTestConfiguration;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.service.TimeEntryTrackingServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@WebMvcTest(TimeEntryTrackingController.class) /* since this is not in integration test, rather controller unit test */
@EnableConfigurationProperties /* to use application.yml-test file */
@ActiveProfiles("test")
@AutoConfigureMockMvc/* calling the api itself */
@ContextConfiguration(classes = {EurekaClientTestConfiguration.class}) /* to call the configuration in the test (for service-registry configs) */
class TimeEntryTrackingControllerTest {

    private @MockBean TimeEntryTrackingServiceImpl timeEntryTrackingService;
    private @MockBean KeycloakSecurityUtil keycloakSecurityUtil;

    private @Autowired MockMvc mockMvc;

    private final String baseUrl = "/api/v1/time";
    private final List<TimeEntryDTO> timeEntries = new ArrayList<>();

    @BeforeEach
    void setUp() {
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO("1", "2024-05-11 08:00:00", "2024-05-11 10:00:00", "02:00:00");

        this.timeEntries.add(timeEntryDTO);
    }

    @Test
    public void startTrackingTimeEntryTest() throws Exception {
        // given
        String timeEntryId = "some_time_entry_id";
        when(this.timeEntryTrackingService.startTrackingTimeEntry())
                .thenReturn(timeEntryId);

        // when
        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl + "/track/start")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("some_authority")))
        );

        // then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("CREATED")))
                .andExpect(jsonPath("$.message", is("Time tracking started...")))
                .andExpect(jsonPath("$.data", is(timeEntryId)))
        ;
    }

    @Test
    public void stopTrackingTimeEntryTest() throws Exception {
        // given
        TimeEntryDTO timeEntryDTO = this.timeEntries.getFirst();
        String timeEntryId = timeEntryDTO.timeEntryId();
        when(this.timeEntryTrackingService.stopTrackingTimeEntry(timeEntryId))
                .thenReturn(timeEntryDTO);

        // when
        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/track/stop/" + timeEntryId)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("some_authority")))
        );

        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.httpStatus", is("OK")))
                .andExpect(jsonPath("$.message", is("Time tracking stopped.")))
                .andExpect(jsonPath("$.data.id", is(timeEntryId)))
                .andExpect(jsonPath("$.data.startTime", is(timeEntryDTO.startTime())))
                .andExpect(jsonPath("$.data.endTime", is(timeEntryDTO.endTime())))
                .andExpect(jsonPath("$.data.duration", is(timeEntryDTO.duration())))
        ;
    }

}