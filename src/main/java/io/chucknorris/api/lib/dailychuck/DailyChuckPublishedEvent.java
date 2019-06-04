package io.chucknorris.api.lib.dailychuck;

import io.chucknorris.api.lib.event.BaseEvent;

public class DailyChuckPublishedEvent extends BaseEvent {
    public DailyChuckPublishedEvent(DailyChuckIssue dailyChuckIssue) {
        super("DailyChuckPublishedEvent", dailyChuckIssue);
    }
}
