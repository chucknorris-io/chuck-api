package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AccessToken implements Serializable {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("team_id")
  private String teamId;

  @JsonProperty("team_name")
  private String teamName;

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("user_name")
  private String userName;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
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

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}