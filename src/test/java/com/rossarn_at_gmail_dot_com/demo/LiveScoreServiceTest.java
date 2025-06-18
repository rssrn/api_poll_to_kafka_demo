package com.rossarn_at_gmail_dot_com.demo;

import com.rossarn_at_gmail_dot_com.demo.model.EventStatus;
import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import com.rossarn_at_gmail_dot_com.demo.observed.SportEventUpdated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LiveScoreServiceTest {
    @Autowired
    private LiveScoreService liveScoreService;

    @Test
    public void scoreLocalStoreRetainsState() {
        // inform service of live event
        EventStatus eventStatus = new EventStatus("2468", true);
        liveScoreService.processEventStatus(eventStatus);

        // send in a score
        SportEventUpdated e = new SportEventUpdated(new Object(), new SportEvent("2468", "0:0"));
        liveScoreService.handleSportEventUpdated(e);

        // score correct in local store
        assertEquals(liveScoreService.getLiveScore("2468"), "0:0");

        // finish the event
        EventStatus eventStatusClosed = new EventStatus("2468", false);
        liveScoreService.processEventStatus(eventStatusClosed);

        // live score removed from local store
        assertNull(liveScoreService.getLiveScore("2468"));
    }

}