package com.seyed.ali.timeentryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyed.ali.timeentryservice.config.EurekaClientTestConfiguration;
import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import com.seyed.ali.timeentryservice.model.payload.TimeBillingDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
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

    private final String baseUrl = "/api/v1/time";
    private final List<TimeEntryDTO> timeEntries = new ArrayList<>();
    private @MockBean TimeEntryTrackingServiceImpl timeEntryTrackingService;
    private @MockBean KeycloakSecurityUtil keycloakSecurityUtil;
    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO("1", "2024-05-11 08:00:00", "2024-05-11 10:00:00", false, BigDecimal.ZERO.toString(), "02:00:00", "1", "1");

        this.timeEntries.add(timeEntryDTO);
    }

    @Test
    public void startTrackingTimeEntryTest() throws Exception {
        // given
        String timeEntryId = "some_time_entry_id";
        when(this.timeEntryTrackingService.startTrackingTimeEntry(isA(TimeBillingDTO.class)))
                .thenReturn(timeEntryId);
        String json = this.objectMapper.writeValueAsString(new TimeBillingDTO(true, BigDecimal.ONE, "1", "1"));

        // when
        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl + "/track/start")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json)
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
        String timeEntryId = timeEntryDTO.getTimeEntryId();
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
                .andExpect(jsonPath("$.data.timeEntryId", is("1")))
                .andExpect(jsonPath("$.data.startTime", is("2024-05-11 08:00:00")))
                .andExpect(jsonPath("$.data.endTime", is("2024-05-11 10:00:00")))
                .andExpect(jsonPath("$.data.duration", is("02:00:00")))
        ;
    }

}