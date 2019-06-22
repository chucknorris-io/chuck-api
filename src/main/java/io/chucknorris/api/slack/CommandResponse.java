package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CommandResponse implements SlackCommandResponse, Serializable {

  @JsonProperty("icon_url")
  private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

  @JsonProperty("text")
  private String text;

  @JsonProperty("attachments")
  private SlackCommandResponseAttachment[] attachments;

  @JsonProperty("response_type")
  private String responseType = ResponseType.IN_CHANNEL;

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public SlackCommandResponseAttachment[] getAttachments() {
    return attachments;
  }

  public void setAttachments(SlackCommandResponseAttachment[] attachments) {
    this.attachments = attachments;
  }

  public String getResponseType() {
    return responseType;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }
}
