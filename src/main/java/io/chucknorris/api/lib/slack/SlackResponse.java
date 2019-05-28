package io.chucknorris.api.lib.slack;

import io.chucknorris.api.lib.slack.Impl.ResponseAttachment;

public interface SlackResponse {
    ResponseAttachment[] getAttachments();

    String getIconUrl();

    String getResponseType();

    String getText();
}
