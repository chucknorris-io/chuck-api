package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CommandResponseAttachment implements Serializable, SlackCommandResponseAttachment {

  @JsonProperty("fallback")
  private String fallback;

  @JsonProperty("mrkdwn_in")
  private String[] mrkdownIn = new String[]{"text"};

  @JsonProperty("text")
  private String text;

  @JsonProperty("title")
  private String title;

  @JsonProperty("title_link")
  private String titleLink;

  public String getFallback() {
    return fallback;
  }

  public void setFallback(String fallback) {
    this.fallback = fallback;
  }

  public String[] getMrkdownIn() {
    return mrkdownIn;
  }

  public void setMrkdownIn(String[] mrkdownIn) {
    this.mrkdownIn = mrkdownIn;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitleLink() {
    return titleLink;
  }

  public void setTitleLink(String titleLink) {
    this.titleLink = titleLink;
  }
}
