package io.chucknorris.lib.mailchimp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

public class MailingListStatistic implements Serializable {

  @JsonProperty("member_count")
  private Integer memberCount;

  @JsonProperty("unsubscribe_count")
  private Integer unsubscribeCount;

  @JsonProperty("cleaned_count")
  private Integer cleanedCount;

  @JsonProperty("member_count_since_send")
  private Integer memberCountSinceSend;

  @JsonProperty("unsubscribe_count_since_send")
  private Integer unsubscribeCountSinceSend;

  @JsonProperty("cleaned_count_since_send")
  private Integer cleanedCountSinceSend;

  @JsonProperty("campaign_count")
  private Integer campaignCount;

  @JsonProperty("campaign_last_sent")
  private Date campaignLastSent;

  @JsonProperty("merge_field_count")
  private Integer mergeFieldCount;

  @JsonProperty("avg_sub_rate")
  private Integer avgSubRate;

  @JsonProperty("avg_unsub_rate")
  private Integer avgUnsubRate;

  @JsonProperty("target_sub_rate")
  private Integer targetSubRate;

  @JsonProperty("openRate")
  private Float openRate;

  @JsonProperty("click_rate")
  private Float clickRate;

  @JsonProperty("last_sub_date")
  private Date lastDubDate;

  @JsonProperty("last_unsub_date")
  private Date lastUnsubDate;

  public Integer getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Integer memberCount) {
    this.memberCount = memberCount;
  }

  public Integer getUnsubscribeCount() {
    return unsubscribeCount;
  }

  public void setUnsubscribeCount(Integer unsubscribeCount) {
    this.unsubscribeCount = unsubscribeCount;
  }

  public Integer getCleanedCount() {
    return cleanedCount;
  }

  public void setCleanedCount(Integer cleanedCount) {
    this.cleanedCount = cleanedCount;
  }

  public Integer getMemberCountSinceSend() {
    return memberCountSinceSend;
  }

  public void setMemberCountSinceSend(Integer memberCountSinceSend) {
    this.memberCountSinceSend = memberCountSinceSend;
  }

  public Integer getUnsubscribeCountSinceSend() {
    return unsubscribeCountSinceSend;
  }

  public void setUnsubscribeCountSinceSend(Integer unsubscribeCountSinceSend) {
    this.unsubscribeCountSinceSend = unsubscribeCountSinceSend;
  }

  public Integer getCleanedCountSinceSend() {
    return cleanedCountSinceSend;
  }

  public void setCleanedCountSinceSend(Integer cleanedCountSinceSend) {
    this.cleanedCountSinceSend = cleanedCountSinceSend;
  }

  public Integer getCampaignCount() {
    return campaignCount;
  }

  public void setCampaignCount(Integer campaignCount) {
    this.campaignCount = campaignCount;
  }

  public Date getCampaignLastSent() {
    return campaignLastSent;
  }

  public void setCampaignLastSent(Date campaignLastSent) {
    this.campaignLastSent = campaignLastSent;
  }

  public Integer getMergeFieldCount() {
    return mergeFieldCount;
  }

  public void setMergeFieldCount(Integer mergeFieldCount) {
    this.mergeFieldCount = mergeFieldCount;
  }

  public Integer getAvgSubRate() {
    return avgSubRate;
  }

  public void setAvgSubRate(Integer avgSubRate) {
    this.avgSubRate = avgSubRate;
  }

  public Integer getAvgUnsubRate() {
    return avgUnsubRate;
  }

  public void setAvgUnsubRate(Integer avgUnsubRate) {
    this.avgUnsubRate = avgUnsubRate;
  }

  public Integer getTargetSubRate() {
    return targetSubRate;
  }

  public void setTargetSubRate(Integer targetSubRate) {
    this.targetSubRate = targetSubRate;
  }

  public Float getOpenRate() {
    return openRate;
  }

  public void setOpenRate(Float openRate) {
    this.openRate = openRate;
  }

  public Float getClickRate() {
    return clickRate;
  }

  public void setClickRate(Float clickRate) {
    this.clickRate = clickRate;
  }

  public Date getLastDubDate() {
    return lastDubDate;
  }

  public void setLastDubDate(Date lastDubDate) {
    this.lastDubDate = lastDubDate;
  }

  public Date getLastUnsubDate() {
    return lastUnsubDate;
  }

  public void setLastUnsubDate(Date lastUnsubDate) {
    this.lastUnsubDate = lastUnsubDate;
  }
}
