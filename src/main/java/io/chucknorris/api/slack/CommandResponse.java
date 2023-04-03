package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class CommandResponse implements SlackCommandResponse, Serializable {

  @JsonProperty("icon_url")
  private String iconUrl = "https://api.chucknorris.io/img/avatar/chuck-norris.png";

  @JsonProperty("text")
  private String text;

  @JsonProperty("attachments")
  private SlackCommandResponseAttachment[] attachments;

  @JsonProperty("response_type")
  private String responseType = ResponseType.IN_CHANNEL;
}
