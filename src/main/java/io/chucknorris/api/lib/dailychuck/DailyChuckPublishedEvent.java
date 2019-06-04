package io.chucknorris.api.lib.dailychuck;

import io.chucknorris.api.lib.event.Event;

public class DailyChuckPublishedEvent implements Event {

    final String name = "DailyChuckPublishedEvent";
    final Object payload;

    public DailyChuckPublishedEvent(DailyChuckIssue dailyChuckIssue) {
        this.payload = dailyChuckIssue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
