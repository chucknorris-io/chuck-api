package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Request implements Serializable {

  @JsonProperty("channel_id")
  private String channelId;

  @JsonProperty("channel_name")
  private String channelName;

  /**
   * The command that was typed in to trigger this request. This value can be useful if you want to
   * use a single Request URL to service multiple Slash Commands, as it lets you tell them apart.
   */
  @JsonProperty("command")
  private String command;

  @JsonProperty("enterprise_id")
  private String enterpriseId;

  @JsonProperty("enterprise_name")
  private String enterpriseName;

  /**
   * A URL that you can use to respond to the command.
   */
  @JsonProperty("enterprise_url")
  private String responseUrl;

  @JsonProperty("team_domain")
  private String teamDomain;

  @JsonProperty("team_id")
  private String teamId;

  /**
   * This is the part of the Slash Command after the command itself, and it can contain absolutely
   * anything that the user might decide to type. It is common to use this text parameter to provide
   * extra context for the command.
   */
  @JsonProperty("text")
  private String text;

  /**
   * This is a verification token, a deprecated feature that you shouldn't use any more. It was used
   * to verify that requests were legitimately being sent by Slack to your app, but you should use
   * the signed secrets functionality to do this instead.
   */
  @JsonProperty("token")
  private String token;

  /**
   * If you need to respond to the command by opening a dialog, you'll need this trigger ID to get
   * it to work. You can use this ID with dialog.open up to 3000ms after this data payload is sent.
   */
  @JsonProperty("trigger_id")
  private String triggerId;

  /**
   * The ID of the user who triggered the command.
   */
  @JsonProperty("user_id")
  private String userId;

  /**
   * The plain text name of the user who triggered the command. As above, do not rely on this field
   * as it is being phased out, use the user_id instead.
   */
  @JsonProperty("user_name")
  private String userName;

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Request{");
    sb.append("channelId='").append(channelId).append('\'');
    sb.append(", channelName='").append(channelName).append('\'');
    sb.append(", command='").append(command).append('\'');
    sb.append(", enterpriseId='").append(enterpriseId).append('\'');
    sb.append(", enterpriseName='").append(enterpriseName).append('\'');
    sb.append(", responseUrl='").append(responseUrl).append('\'');
    sb.append(", teamDomain='").append(teamDomain).append('\'');
    sb.append(", teamId='").append(teamId).append('\'');
    sb.append(", text='").append(text).append('\'');
    sb.append(", token='").append(token).append('\'');
    sb.append(", triggerId='").append(triggerId).append('\'');
    sb.append(", userId='").append(userId).append('\'');
    sb.append(", userName='").append(userName).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getEnterpriseId() {
    return enterpriseId;
  }

  public void setEnterpriseId(String enterpriseId) {
    this.enterpriseId = enterpriseId;
  }

  public String getEnterpriseName() {
    return enterpriseName;
  }

  public void setEnterpriseName(String enterpriseName) {
    this.enterpriseName = enterpriseName;
  }

  public String getResponseUrl() {
    return responseUrl;
  }

  public void setResponseUrl(String responseUrl) {
    this.responseUrl = responseUrl;
  }

  public String getTeamDomain() {
    return teamDomain;
  }

  public void setTeamDomain(String teamDomain) {
    this.teamDomain = teamDomain;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getTriggerId() {
    return triggerId;
  }

  public void setTriggerId(String triggerId) {
    this.triggerId = triggerId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}

