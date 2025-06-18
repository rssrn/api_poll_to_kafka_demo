package com.rossarn_at_gmail_dot_com.demo.kafka;

import com.rossarn_at_gmail_dot_com.demo.LiveScorePoller;
import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import com.rossarn_at_gmail_dot_com.demo.observed.SportEventUpdated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SportsMessageProducerService {

    private static final Logger logger = LogManager.getLogger(LiveScorePoller.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topicName:sports.scores}")
    private String topicName;

    @EventListener
    public void handleSportEventUpdated(SportEventUpdated observedEvent) {
        SportEvent sportEvent = observedEvent.getEvent();
        sendMessage(sportEvent);
    }

    private void sendMessage(SportEvent sportEvent) {
        // SportEvent includes a field we don't want to publish (isLive) so constructing payload to spec
        JSONObject payload = new JSONObject();
        try {
            payload.put("eventId", sportEvent.eventId());
            payload.put("currentScore", sportEvent.currentScore());
        } catch (JSONException e) {
            logger.error("Failed to construct payload for event: {}", sportEvent);
        }

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, payload.toString());
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message {} with offset {}", payload, result.getRecordMetadata().offset());
            } else {
                logger.info("Unable to send message {} exception is: {}", payload, ex.getMessage());
            }
        });
    }
}
