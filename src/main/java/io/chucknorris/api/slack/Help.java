package io.chucknorris.api.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Help implements SlackCommandResponse {

  @JsonProperty("attachments")
  private CommandResponseAttachment[] attachments;

  @JsonProperty("icon_url")
  private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

  @JsonProperty("response_type")
  private String responseType = ResponseType.EPHEMERAL;

  @JsonProperty("text")
  private String text = "*Available commands:*";

  /**
   * Instantiates a new Help {@link Help}.
   */
  public Help() {
    CommandResponseAttachment newsletter = new CommandResponseAttachment();
    newsletter.setTitle("The Daily Chuck");
    newsletter.setText(
        ":facepunch: Sign up for *The Daily Chuck* and get your daily dose of the best #ChuckNorrisFacts every morning straight int your inbox! https://mailchi.mp/5a19a2898bf7/the-daily-chuck");

    CommandResponseAttachment randomJoke = new CommandResponseAttachment();
    randomJoke.setText("Type `/chuck` to get a random joke.");
    randomJoke.setTitle("Random joke");

    CommandResponseAttachment search = new CommandResponseAttachment();
    search.setText(
        "Type `/chuck ? {search_term}` to search within tens of thousands Chuck Norris jokes.");
    search.setTitle("Free text search");

    CommandResponseAttachment randomJokePersonalized = new CommandResponseAttachment();
    randomJokePersonalized
        .setText("Type `/chuck @ {user_name}` to get a random personalized joke.");
    randomJokePersonalized.setTitle("Random personalized joke");

    CommandResponseAttachment randomJokeFromCategory = new CommandResponseAttachment();
    randomJokeFromCategory.setText(
        "Type `/chuck {category_name}` to get a random joke from within a given category.");
    randomJokeFromCategory.setTitle("Random joke from category");

    CommandResponseAttachment categories = new CommandResponseAttachment();
    categories.setText("Type `/chuck -cat` to retrieve a list of all categories.");
    categories.setTitle("Categories");

    CommandResponseAttachment jokeById = new CommandResponseAttachment();
    jokeById.setText("Type `/chuck : {joke_id}` to retrieve get a joke by a given `id`.");
    jokeById.setTitle("Get joke by id");

    CommandResponseAttachment help = new CommandResponseAttachment();
    help.setText("Type `/chuck help` to display a list of available commands.");
    help.setTitle("Help");

    attachments = new CommandResponseAttachment[]{
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
  public CommandResponseAttachment[] getAttachments() {
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
