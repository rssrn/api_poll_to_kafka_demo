package com.rossarn_at_gmail_dot_com.demo;

import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import com.rossarn_at_gmail_dot_com.demo.observed.SportEventUpdated;
import com.rossarn_at_gmail_dot_com.demo.sportsapi.SportsAPICaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

/***
 * Regularly poll scores for one event
 */
public class LiveScorePoller implements Runnable {
    private static final Logger logger = LogManager.getLogger(LiveScorePoller.class);

    private ApplicationEventPublisher publisher;
    private SportsAPICaller api;

    private final SportEvent target;

    public LiveScorePoller(SportEvent target, SportsAPICaller api, ApplicationEventPublisher publisher) {
        this.target = target;
        this.api = api;
        this.publisher = publisher;
    }

    @Override
    public void run() {
        logger.info("Polling for event " + target.eventId());
        try {
            SportEvent event = api.getEvent(target.eventId());
            if (event != null) {
                logger.info("Poll for event " + target.eventId() + " got " + event);
                publisher.publishEvent(new SportEventUpdated(this, event));
            }
        } catch (Exception e) {
            System.out.println("Poll failure " + e.toString());
        }

    }
}
