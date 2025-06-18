package com.rossarn_at_gmail_dot_com.demo;

import com.rossarn_at_gmail_dot_com.demo.model.EventStatus;
import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import com.rossarn_at_gmail_dot_com.demo.observed.SportEventUpdated;
import com.rossarn_at_gmail_dot_com.demo.sportsapi.SportsAPICaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/***
 * Maintain internal model of all live sport events and their scores
 */
@Component
public class LiveScoreService {
    private static final Logger logger = LogManager.getLogger(LiveScoreService.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private SportsAPICaller api;

    private Map<String, SportEvent> liveEvents = new HashMap<>();
    private Map<String, ScheduledFuture<?>> pollers = new HashMap<>();

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Value("${scheduler.event.interval:10000}")
    private int eventPollInterval;

    /***
     * Process event status, update internal store if changed
     */
    public void processEventStatus(EventStatus eventStatus) {

        String id = eventStatus.eventId();
        if (eventStatus.status() && !liveEvents.containsKey(id)) {
            sportEventStarted(id);
        } else if (!eventStatus.status() && liveEvents.containsKey(id)) {
            sportEventFinished(id);
        } else {
            logger.info("No state change: {}", eventStatus);
        }
    }

    /***
     * Process event score, update internal store if changed
     */
    @EventListener
    public void handleSportEventUpdated(SportEventUpdated observedEvent) {
        SportEvent sportEvent = observedEvent.getEvent();
        sportEventScoreUpdated(sportEvent);
    }

    public String getLiveScore(String eventId) {
        if (liveEvents.containsKey(eventId)) {
            return liveEvents.get(eventId).currentScore();
        }
        return null;
    }

    /**
     * Event has moved from not-live (or unknown) to live.  Start polling for live scores for this event, and update internal state.
     */
    private void sportEventStarted(String eventId) {
        logger.info("Event {} newly received in live state", eventId);
        SportEvent newEvent = new SportEvent(eventId, null);
        liveEvents.put(eventId, newEvent);

        // the event is now live, so start polling for live score updates
        LiveScorePoller p = new LiveScorePoller(newEvent, api, publisher);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(p, eventPollInterval, eventPollInterval, TimeUnit.MILLISECONDS);
        pollers.put(eventId, future);
    }

    /**
     * Event has moved from live to not-live.  Stop polling for live scores for this event, and update internal state.
     */
    private void sportEventFinished(String eventId) {
        logger.info("Event {} is no longer in live state", eventId);
        liveEvents.remove(eventId);

        pollers.get(eventId).cancel(false);
    }

    /***
     * New score received for event.  Update internal state including the score.
     */
    private void sportEventScoreUpdated(SportEvent sportEvent) {

        // update internal score with the newly received score
        String id = sportEvent.eventId();
        logger.info("Event {} score is now {}", id, sportEvent.currentScore());
        if (liveEvents.containsKey(id)) {
            // we agree the event is live, did its score change?
            if (!sportEvent.currentScore().equals(liveEvents.get(id).currentScore())) {
                logger.info("Received NEW score {} for event {}", sportEvent.currentScore(), id);
                liveEvents.replace(id, sportEvent);
            } else {
                logger.info("Received UNCHANGED score {} for event {}", sportEvent.currentScore(), id);
            }
        } else {
            logger.error("Received score for event {} but that event is not found in the live event store", id);
        }
    }
}
