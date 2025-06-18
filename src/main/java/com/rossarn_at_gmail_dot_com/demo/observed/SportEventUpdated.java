package com.rossarn_at_gmail_dot_com.demo.observed;

import com.rossarn_at_gmail_dot_com.demo.model.SportEvent;
import org.springframework.context.ApplicationEvent;


public class SportEventUpdated extends ApplicationEvent {
    private final SportEvent event;

    public SportEventUpdated(Object source, SportEvent event) {
        super(source);
        this.event = event;
    }

    public SportEvent getEvent() {
        return event;
    }
}
