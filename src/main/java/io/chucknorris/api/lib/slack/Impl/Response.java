package io.chucknorris.api.lib.slack.Impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.chucknorris.api.lib.slack.SlackResponse;

import java.io.Serializable;

public class Response implements SlackResponse, Serializable {
    @JsonProperty("icon_url")
    private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

    @JsonProperty("text")
    private String text;

    @JsonProperty("attachments")
    private ResponseAttachment[] attachments;

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

    public ResponseAttachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(ResponseAttachment[] attachments) {
        this.attachments = attachments;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
}
