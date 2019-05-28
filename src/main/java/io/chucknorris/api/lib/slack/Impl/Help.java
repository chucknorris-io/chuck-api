package io.chucknorris.api.lib.slack.Impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.chucknorris.api.lib.slack.SlackResponse;

public class Help implements SlackResponse {
    @JsonProperty("attachments")
    private ResponseAttachment[] attachments;

    @JsonProperty("icon_url")
    private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

    @JsonProperty("response_type")
    private String responseType = ResponseType.EPHEMERAL;

    @JsonProperty("text")
    private String text = "*Available commands:*";

    public Help() {
        ResponseAttachment newsletter = new ResponseAttachment();
        newsletter.setTitle("The Daily Chuck");
        newsletter.setText(":facepunch: Sign up for *The Daily Chuck* and get your daily dose of the best #ChuckNorrisFacts every morning straight int your inbox! https://mailchi.mp/5a19a2898bf7/the-daily-chuck");

        ResponseAttachment randomJoke = new ResponseAttachment();
        randomJoke.setText("Type `/chuck` to get a random joke.");
        randomJoke.setTitle("Random joke");

        ResponseAttachment search = new ResponseAttachment();
        search.setText("Type `/chuck ? {search_term}` to search within tens of thousands Chuck Norris jokes.");
        search.setTitle("Free text search");

        ResponseAttachment randomJokePersonalized = new ResponseAttachment();
        randomJokePersonalized.setText("Type `/chuck @ {user_name}` to get a random personalized joke.");
        randomJokePersonalized.setTitle("Random personalized joke");

        ResponseAttachment randomJokeFromCategory = new ResponseAttachment();
        randomJokeFromCategory.setText("Type `/chuck {category_name}` to get a random joke from within a given category.");
        randomJokeFromCategory.setTitle("Random joke from category");

        ResponseAttachment categories = new ResponseAttachment();
        categories.setText("Type `/chuck -cat` to retrieve a list of all categories.");
        categories.setTitle("Categories");

        ResponseAttachment jokeById = new ResponseAttachment();
        jokeById.setText("Type `/chuck : {joke_id}` to retrieve get a joke by a given `id`.");
        jokeById.setTitle("Get joke by id");

        ResponseAttachment help = new ResponseAttachment();
        help.setText("Type `/chuck help` to display a list of available commands.");
        help.setTitle("Help");

        attachments = new ResponseAttachment[]{
                newsletter,
                randomJoke,
                search,
                randomJokePersonalized,
                randomJokeFromCategory,
                categories,
                jokeById,
                help
        };
    }

    @Override
    public ResponseAttachment[] getAttachments() {
        return attachments;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public String getResponseType() {
        return responseType;
    }

    @Override
    public String getText() {
        return text;
    }
}
