package com.rossarn_at_gmail_dot_com.demo.sportsapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import com.rossarn_at_gmail_dot_com.demo.sportsapi.model.ExtEventScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Handle interactions with external REST api
 */
@Component
public class SportsAPICaller {
    private static Logger logger = LogManager.getLogger(SportsAPICaller.class);

    private RestTemplate restTemplate;

    @Value("${sportsapi.url:http://localhost:8081/eventScore/}")
    private String baseUrl;

    private final ObjectMapper objectMapper;

    public SportsAPICaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get current score for an event
     */
    public SportEvent getEvent(String eventId) {
        logger.info("Fetching event " + eventId + " from API");

        ResponseEntity<ExtEventScore> resp = restTemplate.getForEntity(baseUrl + eventId, ExtEventScore.class);
        if (resp.getStatusCode() == HttpStatus.OK) {
            // convert to our internal representation
            return new SportEvent(resp.getBody().eventId(), resp.getBody().currentScore());
        } else {
            logger.error("Bad response from external REST API: {}, failed to update score for event {}", resp.getStatusCode(), eventId);
            return null;
        }
    }
}
