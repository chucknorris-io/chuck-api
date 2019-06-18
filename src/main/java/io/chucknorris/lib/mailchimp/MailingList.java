package io.chucknorris.lib.mailchimp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.chucknorris.lib.mailchimp.MailingListStatistic;
import java.io.Serializable;

public class MailingList implements Serializable {

  @JsonProperty("id")
  private String id;

  @JsonProperty("stats")
  private MailingListStatistic mailingListStatistic;

  @JsonProperty("name")
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MailingListStatistic getMailingListStatistic() {
    return mailingListStatistic;
  }

  public void setMailingListStatistic(MailingListStatistic mailingListStatistic) {
    this.mailingListStatistic = mailingListStatistic;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
