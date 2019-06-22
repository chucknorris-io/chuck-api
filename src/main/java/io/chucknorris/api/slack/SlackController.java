package io.chucknorris.api.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.chucknorris.api.joke.Joke;
import io.chucknorris.api.joke.JokeRepository;
import io.chucknorris.api.joke.JokeService;
import io.chucknorris.lib.event.EventService;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class SlackController {

  private static final Logger logger = LoggerFactory.getLogger(SlackController.class);

  @Value("${application.base_url}")
  private String baseUrl;

  private EventService eventService;
  private JokeRepository jokeRepository;
  private JokeService jokeService;
  private MeterRegistry meterRegistry;
  private SlackService slackService;

  /**
   * Returns a new {@link SlackController} instance.
   */
  public SlackController(
      EventService eventService,
      JokeRepository jokeRepository,
      JokeService jokeService,
      MeterRegistry meterRegistry,
      SlackService slackService
  ) {
    this.eventService = eventService;
    this.jokeRepository = jokeRepository;
    this.jokeService = jokeService;
    this.meterRegistry = meterRegistry;
    this.slackService = slackService;
  }

  /**
   * Returns the model for the connect/slack view.
   */
  public @RequestMapping(
      value = "/connect/slack",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
      produces = MediaType.TEXT_HTML_VALUE
  ) ModelAndView connect(
      @RequestParam(value = "code", required = false) final String code
  ) throws JsonProcessingException {
    AccessToken accessToken = slackService.requestAccessToken(code);

    ModelAndView model = new ModelAndView("connect/slack");
    if (accessToken.getAccessToken() != null) {
      model.setStatus(HttpStatus.OK);
      model.addObject("page_title",
          "Congrats, the app was successfully installed for your Slack team!");
      model.addObject("error", false);
      model.addObject("message", null);

      SlackConnectEvent slackConnectEvent = new SlackConnectEvent(accessToken);
      eventService.publishEvent(slackConnectEvent);

      meterRegistry.counter(
          "application_slack_connect",
          "team_name", accessToken.getTeamName()
      ).increment();
    } else {
      model.setStatus(HttpStatus.UNAUTHORIZED);
      model.addObject("page_title", "Oops, an error has occurred.");
      model.addObject("error", true);
      model.addObject("message", "Oops, an error has occurred. Please try again later!");
    }

    return model;
  }

  /**
   * Returns a {@link SlackCommandResponse}.
   *
   * @param request The slack request {@link Request}
   * @return slackCommandResponse
   */
  public @ResponseBody @RequestMapping(
      value = {"/integration/slack", "/jokes/slack"},
      method = RequestMethod.POST,
      headers = {
          HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
          HttpHeaders.CONTENT_TYPE + "=" + MediaType.APPLICATION_FORM_URLENCODED_VALUE
      },
      produces = MediaType.APPLICATION_JSON_VALUE
  ) SlackCommandResponse command(Request request) {
    logger.info(request.toString());

    if (request.getText() == null || request.getText().isEmpty()) {
      MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
      urlQueryParams.set("utm_source", "slack");
      urlQueryParams.set("utm_medium", "api");
      urlQueryParams.set("utm_term", request.getTeamDomain());
      urlQueryParams.set("utm_campaign", "random+joke");

      Joke joke = jokeService.randomJokeByCategories(
          slackService.getWhitelistedCategories()
      );

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "random_joke",
          "team_name", request.getTeamDomain()
      ).increment();

      return composeJokeResponse(joke, urlQueryParams);
    }

    if (request.getText().equals("help")) {
      return new Help();
    }

    if (request.getText().equals("-cat")) {
      String[] categories = jokeRepository.findAllCategories();
      String[] whitelistedCategories = slackService.filterNonWhitelistedCategories(categories);

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Available categories are: `");
      stringBuilder.append(String.join("`, `", whitelistedCategories));
      stringBuilder.append(
          "`. Type `/chuck {category_name}` to retrieve a "
              + "random joke from within the given category."
      );

      CommandResponse response = new CommandResponse();
      response.setText(stringBuilder.toString());
      response.setResponseType(ResponseType.EPHEMERAL);

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "find_all_categories",
          "team_name", request.getTeamDomain()
      ).increment();

      return response;
    }

    if (request.getText().startsWith(":")) {
      String id = request.getText().substring(1).trim();
      Optional<Joke> joke = jokeRepository.findById(id);

      if (!joke.isPresent()) {
        CommandResponse response = new CommandResponse();
        response.setText("Sorry dude ¯\\_(ツ)_/¯ , no joke with id (\"" + id + "\") found.");
        response.setResponseType(ResponseType.EPHEMERAL);

        return response;
      }

      MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
      urlQueryParams.set("utm_source", "slack");
      urlQueryParams.set("utm_medium", "api");
      urlQueryParams.set("utm_term", request.getTeamDomain());
      urlQueryParams.set("utm_campaign", "joke+by+id");

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "find_joke_by_id",
          "team_name", request.getTeamDomain()
      ).increment();

      return composeJokeResponse(joke.get(), urlQueryParams);
    }

    if (request.getText().startsWith("@")) {
      MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
      urlQueryParams.set("utm_source", "slack");
      urlQueryParams.set("utm_medium", "api");
      urlQueryParams.set("utm_term", request.getTeamDomain());
      urlQueryParams.set("utm_campaign", "random+personalized+joke");

      String[] categories = jokeRepository.findAllCategories();
      String[] whitelistedCategories = slackService.filterNonWhitelistedCategories(categories);

      String substitute = request.getText().substring(1).trim();
      Joke joke = jokeService.randomPersonalizedJokeByCategories(
          substitute,
          whitelistedCategories
      );

      if (joke == null) {
        CommandResponse response = new CommandResponse();
        response.setText("Your search for *\"" + substitute
            + "\"* did not match any joke ¯\\_(ツ)_/¯. Make sure that all words are spelled "
            + "correctly. Try different keywords. Try more general keywords."
        );
        response.setResponseType(ResponseType.EPHEMERAL);
        return response;
      }

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "random_personalized_joke",
          "team_name", request.getTeamDomain()
      ).increment();

      return composeJokeResponse(joke, urlQueryParams);
    }

    if (request.getText().startsWith("?")) {
      String query = "";
      Matcher queryMatcher = Pattern.compile("/?\\s?([a-zA-Z0-9]+)").matcher(request.getText());
      if (queryMatcher.find()) {
        query = queryMatcher.group(0).trim();
      }

      int page = 0;
      int pageDisplayValue = 0; // Using a display value because PageRequest#of is zero-base indexed
      Matcher pageMatcher = Pattern.compile("--page\\s?(\\d+)").matcher(request.getText());
      if (pageMatcher.find()) {
        pageDisplayValue = Integer.parseInt(pageMatcher.group(1).trim());
        page = pageDisplayValue <= 1 ? 0 : pageDisplayValue - 1;
      }

      int itemsPerPage = 5;
      Pageable pageable = PageRequest.of(page, itemsPerPage);

      String[] categories = jokeRepository.findAllCategories();
      String[] whitelistedCategories = slackService.filterNonWhitelistedCategories(categories);
      Page<Joke> jokes = jokeService.searchWithCategoryFilter(
          query,
          whitelistedCategories,
          pageable
      );

      if (jokes.getContent().size() < 1) {
        CommandResponse response = new CommandResponse();
        response.setText("Your search for *\"" + query
            + "\"* did not match any joke ¯\\_(ツ)_/¯. Make sure that all words are spelled "
            + "correctly. Try different keywords. Try more general keywords."
        );
        response.setResponseType(ResponseType.EPHEMERAL);

        return response;
      }

      MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
      urlQueryParams.set("utm_source", "slack");
      urlQueryParams.set("utm_medium", "api");
      urlQueryParams.set("utm_term", request.getTeamDomain());
      urlQueryParams.set("utm_campaign", "search+joke");

      SlackCommandResponseAttachment[] attachments = new CommandResponseAttachment[
          jokes.getContent().size()
          ];
      for (int i = 0; i < jokes.getContent().size(); i++) {
        Joke joke = jokes.getContent().get(i);

        UriComponents uriComponents = UriComponentsBuilder
            .newInstance()
            .scheme("https")
            .host(baseUrl)
            .path("/jokes/" + joke.getId())
            .queryParams(urlQueryParams)
            .build()
            .encode();

        SlackCommandResponseAttachment attachment = new CommandResponseAttachment();
        attachment.setFallback(joke.getValue());
        attachment.setText(joke.getValue());
        attachment.setTitle("(" + ((page * itemsPerPage + 1) + i) + ")");
        attachment.setTitleLink(uriComponents.toUriString());

        attachments[i] = attachment;
      }

      CommandResponse response = new CommandResponse();
      if (!jokes.isLast()) {
        response.setText("*Search results: "
            + (page * itemsPerPage + 1)
            + " - "
            + (page * itemsPerPage + jokes.getContent().size())
            + " of " + jokes.getTotalElements()
            + "*. "
            + "Type `/chuck ? "
            + query
            + " --page "
            + (page + 1 + 1)
            + "` to see more results."
        );
      } else {
        response.setText(
            "*Search results: " + (page * itemsPerPage + 1) + " - " + (page * 5 + jokes
                .getNumberOfElements()) + " of " + jokes.getTotalElements() + "*."
        );
      }

      response.setAttachments(attachments);

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "search_joke",
          "team_name", request.getTeamDomain()
      ).increment();

      return response;
    }

    if (!request.getText().isEmpty()) {
      if (!slackService.isWhitelistedCategory(request.getText())) {
        CommandResponse response = new CommandResponse();
        response.setText(
            "Sorry dude ¯\\_(ツ)_/¯ , the given category (\""
                + request.getText()
                + "\") is not whitelisted. Type `/chuck -cat` to see available categories "
                + "or search by query `/chuck ? {search_term}`"
        );
        response.setResponseType(ResponseType.EPHEMERAL);

        return response;
      }

      String[] categories = jokeRepository.findAllCategories();
      String[] whitelistedCategories = slackService.filterNonWhitelistedCategories(categories);
      if (!Arrays.stream(whitelistedCategories).anyMatch(request.getText()::equals)) {
        CommandResponse response = new CommandResponse();
        response.setText(
            "Sorry dude ¯\\_(ツ)_/¯ , we've found no jokes for the given category (\""
                + request.getText()
                + "\"). Type `/chuck -cat` to see available categories or search by "
                + "query `/chuck ? {search_term}`"
        );
        response.setResponseType(ResponseType.EPHEMERAL);

        return response;
      }

      MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
      urlQueryParams.set("utm_source", "slack");
      urlQueryParams.set("utm_medium", "api");
      urlQueryParams.set("utm_term", request.getTeamDomain());
      urlQueryParams.set("utm_campaign", "random+joke+category");

      Joke joke = jokeService.randomJokeByCategory(request.getText());

      meterRegistry.counter(
          "application_slack_command",
          "command_type", "random_joke_by_category",
          "team_name", request.getTeamDomain()
      ).increment();

      return composeJokeResponse(joke, urlQueryParams);
    }

    return new CommandResponse();
  }

  private SlackCommandResponse composeJokeResponse(
      Joke joke,
      MultiValueMap<String, String> urlParams
  ) {
    UriComponents uriComponents = UriComponentsBuilder
        .newInstance()
        .scheme("https")
        .host(baseUrl)
        .path("/jokes/" + joke.getId())
        .queryParams(urlParams)
        .build()
        .encode();

    SlackCommandResponseAttachment attachment = new CommandResponseAttachment();
    attachment.setFallback(joke.getValue());
    attachment.setText(joke.getValue());
    attachment.setTitle("[permalink]");
    attachment.setTitleLink(uriComponents.toUriString());

    CommandResponse response = new CommandResponse();
    response.setAttachments(
        new SlackCommandResponseAttachment[]{attachment}
    );

    return response;
  }
}
