package io.chucknorris.api.slack;

import io.chucknorris.lib.event.BaseEvent;

public class SlackConnectEvent extends BaseEvent {

  public SlackConnectEvent(AccessToken accessToken) {
    super("SlackConnectEvent", accessToken);
  }
}
