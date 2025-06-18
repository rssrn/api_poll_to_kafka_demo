package com.rossarn_at_gmail_dot_com.demo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SportEventsControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    LiveScoreService liveScoreService;

    @Test
    void validRequestReturnsHttpOk() throws Exception {
        String body = "{\"eventId\": \"4321\", \"status\": true}";

        mvc.perform(post("/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void invalidRequestReturnsHttpBadRequest() throws Exception {
        String body = "invalid json";

        mvc.perform(post("/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

}