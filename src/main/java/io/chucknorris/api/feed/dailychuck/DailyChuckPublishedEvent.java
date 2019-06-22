package io.chucknorris.api.feed.dailychuck;

import io.chucknorris.lib.event.BaseEvent;

public class DailyChuckPublishedEvent extends BaseEvent {

  public DailyChuckPublishedEvent(DailyChuckIssue dailyChuckIssue) {
    super("DailyChuckPublishedEvent", dailyChuckIssue);
  }
}
