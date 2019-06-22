package io.chucknorris.api.slack;

public interface SlackCommandResponse {

  SlackCommandResponseAttachment[] getAttachments();

  String getIconUrl();

  String getResponseType();

  String getText();
}
