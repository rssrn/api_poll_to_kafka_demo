package com.rossarn_at_gmail_dot_com.demo;

import com.rossarn_at_gmail_dot_com.demo.model.EventStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class SportEventsController {
    private static final Logger logger = LogManager.getLogger(SportEventsController.class);

    @Autowired
    LiveScoreService liveScoreService;

    @PostMapping(value = "/status")
    @ResponseStatus(HttpStatus.OK)
    public void setStatus(@RequestBody EventStatus eventStatus) {
        logger.info("REST POST received: {}", eventStatus);
        liveScoreService.processEventStatus(eventStatus);
    }
}
