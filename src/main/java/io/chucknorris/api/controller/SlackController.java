package io.chucknorris.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.chucknorris.api.lib.event.EventService;
import io.chucknorris.api.lib.slack.Impl.*;
import io.chucknorris.api.lib.slack.SlackResponse;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SlackController {

    private static final Logger logger = LoggerFactory.getLogger(SlackController.class);

    @Value("${application.base_url}")
    private String baseUrl;

    private EventService eventService;
    private JokeRepository jokeRepository;
    private SlackService slackService;

    public SlackController(EventService eventService, JokeRepository jokeRepository, SlackService slackService) {
        this.eventService = eventService;
        this.jokeRepository = jokeRepository;
        this.slackService = slackService;
    }

    @RequestMapping(
            value = "/connect/slack",
            method = RequestMethod.GET,
            headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView connect(@RequestParam(value = "code") final String code) throws JsonProcessingException {
        AccessToken accessToken = slackService.requestAccessToken(code);

        ModelAndView model = new ModelAndView("connect/slack");
        if (accessToken.getAccessToken() != null) {
            model.setStatus(HttpStatus.OK);
            model.addObject("page_title", "Congrats, the app was successfully installed for your Slack team!");
            model.addObject("error", false);
            model.addObject("message", null);

            SlackConnectEvent slackConnectEvent = new SlackConnectEvent(accessToken);
            eventService.publishEvent(slackConnectEvent);
        } else {
            model.setStatus(HttpStatus.UNAUTHORIZED);
            model.addObject("page_title", "Oops, an error has occurred.");
            model.addObject("error", true);
            model.addObject("message", "Oops, an error has occurred. Please try again later!");
        }

        return model;
    }

    @RequestMapping(
            value = "/integration/slack",
            method = RequestMethod.POST,
            headers = {
                    HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
                    HttpHeaders.CONTENT_TYPE + "=" + MediaType.APPLICATION_FORM_URLENCODED_VALUE
            },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    SlackResponse command(Request request) {
        logger.info(request.toString());

        if (request.getText() == null || request.getText().isEmpty()) {
            Joke joke = jokeRepository.getRandomJoke();

            MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
            urlQueryParams.set("utm_source", "slack");
            urlQueryParams.set("utm_medium", "api");
            urlQueryParams.set("utm_term", request.getTeamDomain());
            urlQueryParams.set("utm_campaign", "random+joke");

            return composeJokeResponse(joke, urlQueryParams);
        }

        if (request.getText().equals("help")) {
            return new Help();
        }

        if (request.getText().equals("-cat")) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Available categories are: `");
            stringBuilder.append(String.join("`, `", jokeRepository.findAllCategories()));
            stringBuilder.append("`. Type `/chuck {category_name}` to retrieve a random joke from within the given category.");

            Response response = new Response();
            response.setText(stringBuilder.toString());
            response.setResponseType(ResponseType.EPHEMERAL);

            return response;
        }

        if (request.getText().startsWith(":")) {
            String id = request.getText().substring(1).trim();
            Optional<Joke> joke = jokeRepository.findById(id);

            if (!joke.isPresent()) {
                Response response = new Response();
                response.setText("Sorry dude ¯\\_(ツ)_/¯ , no joke with id (\"" + id + "\") found.");
                response.setResponseType(ResponseType.EPHEMERAL);

                return response;
            }

            MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
            urlQueryParams.set("utm_source", "slack");
            urlQueryParams.set("utm_medium", "api");
            urlQueryParams.set("utm_term", request.getTeamDomain());
            urlQueryParams.set("utm_campaign", "joke+by+id");

            return composeJokeResponse(joke.get(), urlQueryParams);
        }

        if (request.getText().startsWith("@")) {
            String substitute = request.getText().substring(1).trim();
            Joke joke = jokeRepository.getRandomPersonalizedJoke(substitute);

            MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
            urlQueryParams.set("utm_source", "slack");
            urlQueryParams.set("utm_medium", "api");
            urlQueryParams.set("utm_term", request.getTeamDomain());
            urlQueryParams.set("utm_campaign", "random+personalized+joke");

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
            Page<Joke> jokes = jokeRepository.findByValueContains(query, pageable);

            if (jokes.getContent().size() < 1) {
                Response response = new Response();
                response.setText("Your search for *\"" + query + "\"* did not match any joke ¯\\_(ツ)_/¯. Make sure that all words are spelled correctly. Try different keywords. Try more general keywords.");
                response.setResponseType(ResponseType.EPHEMERAL);

                return response;
            }

            MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
            urlQueryParams.set("utm_source", "slack");
            urlQueryParams.set("utm_medium", "api");
            urlQueryParams.set("utm_term", request.getTeamDomain());
            urlQueryParams.set("utm_campaign", "search+joke");

            ResponseAttachment[] responseAttachments = new ResponseAttachment[jokes.getContent().size()];
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

                ResponseAttachment responseAttachment = new ResponseAttachment();
                responseAttachment.setFallback(joke.getValue());
                responseAttachment.setText(joke.getValue());
                responseAttachment.setTitle("(" + ((page * itemsPerPage + 1) + i) + ")");
                responseAttachment.setTitleLink(uriComponents.toUriString());

                responseAttachments[i] = responseAttachment;
            }

            Response response = new Response();
            if (!jokes.isLast()) {
                response.setText(
                        "*Search results: " + (page * itemsPerPage + 1) + " - " + (page * itemsPerPage + jokes.getContent().size()) + " of " + jokes.getTotalElements() + "*. " +
                                "Type `/chuck ? " + query + " --page " + (page + 1 + 1) + "` to see more results."
                );
            } else {
                response.setText(
                        "*Search results: " + (page * itemsPerPage + 1) + " - " + (page * 5 + jokes.getNumberOfElements()) + " of " + jokes.getTotalElements() + "*."
                );
            }

            response.setAttachments(responseAttachments);

            return response;
        }

        if (!request.getText().isEmpty()) {
            String[] categories = jokeRepository.findAllCategories();
            if (!Arrays.stream(categories).anyMatch(request.getText()::equals)) {
                Response response = new Response();
                response.setText("Sorry dude ¯\\_(ツ)_/¯ , we've found no jokes for the given category (\"" + request.getText() + "\"). Type `/chuck -cat` to see available categories or search by query `/chuck ? {search_term}`");
                response.setResponseType(ResponseType.EPHEMERAL);

                return response;
            }

            Joke joke = jokeRepository.getRandomJokeByCategory(request.getText());

            MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
            urlQueryParams.set("utm_source", "slack");
            urlQueryParams.set("utm_medium", "api");
            urlQueryParams.set("utm_term", request.getTeamDomain());
            urlQueryParams.set("utm_campaign", "random+joke+category");

            return composeJokeResponse(joke, urlQueryParams);
        }

        return new Response();
    }

    private Response composeJokeResponse(Joke joke, MultiValueMap<String, String> urlParams) {
        Response response = new Response();

        UriComponents uriComponents = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(baseUrl)
                .path("/jokes/" + joke.getId())
                .queryParams(urlParams)
                .build()
                .encode();

        ResponseAttachment responseAttachment = new ResponseAttachment();
        responseAttachment.setFallback(joke.getValue());
        responseAttachment.setText(joke.getValue());
        responseAttachment.setTitle("[permalink]");
        responseAttachment.setTitleLink(uriComponents.toUriString());
        response.setAttachments(new ResponseAttachment[]{responseAttachment});

        return response;
    }
}
