package io.chucknorris.api.slack;

public interface SlackCommandResponseAttachment {

  String getFallback();

  void setFallback(String fallback);

  String[] getMrkdownIn();

  void setMrkdownIn(String[] mrkdownIn);

  String getText();

  void setText(String text);

  String getTitle();

  void setTitle(String title);

  String getTitleLink();

  void setTitleLink(String titleLink);
}
