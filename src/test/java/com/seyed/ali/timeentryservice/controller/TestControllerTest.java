package com.seyed.ali.timeentryservice.controller;

import com.seyed.ali.timeentryservice.config.EurekaClientTestConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest({"server.port=0"}) /* random port: `webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT` fails while trying to run the test */
@EnableConfigurationProperties /* to use application.yml-test file */
@ActiveProfiles("test")
@AutoConfigureMockMvc /* calling the api itself */
@ContextConfiguration(classes = {EurekaClientTestConfiguration.class}) /* to call the configuration in the test (for service-registry configs) */
public class TestControllerTest {

    private @Autowired MockMvc mockMvc;

    @Test
    public void helloTest() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/hello")
                        .with(jwt().authorities(new SimpleGrantedAuthority("board_manager")))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", CoreMatchers.is("Hello World")));
    }

    @Test
    public void helloTest_AccessDenied() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/hello")
                        .with(jwt().authorities(new SimpleGrantedAuthority("another_role")))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.httpStatus", is("FORBIDDEN")))
                .andExpect(jsonPath("$.message", is("No permission.")))
                .andExpect(jsonPath("$.data", is("ServerMessage - Access Denied")))
        ;
    }

}
