package io.chucknorris.api.slack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.chucknorris.api.joke.Joke;
import io.chucknorris.api.joke.JokeRepository;
import io.chucknorris.api.joke.JokeService;
import io.chucknorris.lib.event.EventService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

@RunWith(MockitoJUnitRunner.class)
public class SlackControllerTest {

    private static String iconUrl, jokeId, jokeValue;
    private static Joke joke;

    @Mock
    private Counter counter;

    @Mock
    private EventService eventService;

    @Mock
    JokeService jokeService;

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private SlackController slackController;

    @Mock
    private SlackService slackService;

    private String[] whiteListedCategories = new String[]{
        "career",
        "dev",
        "fashion",
        "food",
        "money",
        "movie",
        "travel"
    };

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(slackController, "baseUrl", "localhost");

        iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";
        jokeId = "bg_h3xursougaxzprcrl0q";
        jokeValue = "Chuck Norris programs do not accept input.";
        joke = new Joke()
            .setCategories(new String[]{"dev"})
            .setId(jokeId)
            .setValue(jokeValue);

        when(meterRegistry.counter(
            anyString(), anyString(), anyString()
        )).thenReturn(counter);

        when(meterRegistry.counter(
            anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(counter);

        when(
            slackService.getWhitelistedCategories()
        ).thenReturn(
            whiteListedCategories
        );

        when(
            slackService.filterNonWhitelistedCategories(new String[]{"dev", "fashion", "food"})
        ).thenReturn(
            new String[]{"dev", "fashion", "food"}
        );

        when(
            slackService.filterNonWhitelistedCategories(new String[]{"dev", "explicit", "fashion", "food"})
        ).thenReturn(
            new String[]{"dev", "fashion", "food"}
        );
    }

    @Test
    public void testConnect() throws JsonProcessingException {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken("23BE2D81-35B6-4B73-BCC9-8B6731D2540E");
        accessToken.setTeamName("ACME");

        when(slackService.requestAccessToken("my-super-secret-code")).thenReturn(accessToken);
        when(eventService.publishEvent(any(SlackConnectEvent.class))).thenReturn(null);

        ModelAndView view = slackController.connect("my-super-secret-code");
        assertEquals(HttpStatus.OK, view.getStatus());
        assertEquals("Congrats, the app was successfully installed for your Slack team!", view.getModel().get("page_title"));
        assertEquals(false, view.getModel().get("error"));
        assertEquals(null, view.getModel().get("message"));

        verify(slackService, times(1)).requestAccessToken("my-super-secret-code");
        verifyNoMoreInteractions(slackService);

        verify(eventService, times(1)).publishEvent(any(SlackConnectEvent.class));
        verifyNoMoreInteractions(eventService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_connect", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);
        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testConnectSetsErrorIfAuthenticationTokenIsNull() throws JsonProcessingException {
        AccessToken accessToken = new AccessToken();

        when(slackService.requestAccessToken("my-super-secret-code")).thenReturn(accessToken);

        ModelAndView view = slackController.connect("my-super-secret-code");
        assertEquals(HttpStatus.UNAUTHORIZED, view.getStatus());
        assertEquals("Oops, an error has occurred.", view.getModel().get("page_title"));
        assertEquals(true, view.getModel().get("error"));
        assertEquals("Oops, an error has occurred. Please try again later!", view.getModel().get("message"));

        verify(slackService, times(1)).requestAccessToken("my-super-secret-code");
        verifyNoMoreInteractions(slackService);

        verify(eventService, times(0)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testReturnHelpIfTextEqualsHelp() {
        Request request = new Request();
        request.setText("help");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Available commands:*", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        SlackCommandResponseAttachment newsletter = response.getAttachments()[0];
        assertEquals(null, newsletter.getFallback());
        assertEquals(":facepunch: Sign up for *The Daily Chuck* and get your daily dose of the best #ChuckNorrisFacts every morning straight int your inbox! https://mailchi.mp/5a19a2898bf7/the-daily-chuck", newsletter.getText());
        assertEquals("The Daily Chuck", newsletter.getTitle());
        assertEquals(null, newsletter.getTitleLink());
        assertArrayEquals(new String[]{"text"}, newsletter.getMrkdownIn());

        SlackCommandResponseAttachment randomJoke = response.getAttachments()[1];
        assertEquals(null, randomJoke.getFallback());
        assertEquals("Type `/chuck` to get a random joke.", randomJoke.getText());
        assertEquals("Random joke", randomJoke.getTitle());
        assertEquals(null, randomJoke.getTitleLink());
        assertArrayEquals(new String[]{"text"}, randomJoke.getMrkdownIn());

        SlackCommandResponseAttachment search = response.getAttachments()[2];
        assertEquals(null, search.getFallback());
        assertEquals("Type `/chuck ? {search_term}` to search within tens of thousands Chuck Norris jokes.", search.getText());
        assertEquals("Free text search", search.getTitle());
        assertEquals(null, search.getTitleLink());
        assertArrayEquals(new String[]{"text"}, search.getMrkdownIn());

        SlackCommandResponseAttachment randomJokePersonalized = response.getAttachments()[3];
        assertEquals(null, randomJokePersonalized.getFallback());
        assertEquals("Type `/chuck @ {user_name}` to get a random personalized joke.", randomJokePersonalized.getText());
        assertEquals("Random personalized joke", randomJokePersonalized.getTitle());
        assertEquals(null, randomJokePersonalized.getTitleLink());
        assertArrayEquals(new String[]{"text"}, randomJokePersonalized.getMrkdownIn());

        SlackCommandResponseAttachment randomJokeFromCategory = response.getAttachments()[4];
        assertEquals(null, randomJokeFromCategory.getFallback());
        assertEquals("Type `/chuck {category_name}` to get a random joke from within a given category.", randomJokeFromCategory.getText());
        assertEquals("Random joke from category", randomJokeFromCategory.getTitle());
        assertEquals(null, randomJokeFromCategory.getTitleLink());
        assertArrayEquals(new String[]{"text"}, randomJokeFromCategory.getMrkdownIn());

        SlackCommandResponseAttachment categories = response.getAttachments()[5];
        assertEquals(null, categories.getFallback());
        assertEquals("Type `/chuck -cat` to retrieve a list of all categories.", categories.getText());
        assertEquals("Categories", categories.getTitle());
        assertEquals(null, categories.getTitleLink());
        assertArrayEquals(new String[]{"text"}, categories.getMrkdownIn());

        SlackCommandResponseAttachment help = response.getAttachments()[6];
        assertEquals(null, help.getFallback());
        assertEquals("Type `/chuck : {joke_id}` to retrieve get a joke by a given `id`.", help.getText());
        assertEquals("Get joke by id", help.getTitle());
        assertEquals(null, help.getTitleLink());
        assertArrayEquals(new String[]{"text"}, help.getMrkdownIn());

        SlackCommandResponseAttachment jokeById = response.getAttachments()[7];
        assertEquals(null, jokeById.getFallback());
        assertEquals("Type `/chuck help` to display a list of available commands.", jokeById.getText());
        assertEquals("Help", jokeById.getTitle());
        assertEquals(null, jokeById.getTitleLink());
        assertArrayEquals(new String[]{"text"}, jokeById.getMrkdownIn());

        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomJokeIfTextIsEmpty() {
        when(jokeService.randomJokeByCategories(whiteListedCategories)).thenReturn(joke);

        Request request = new Request();
        request.setText("");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, commandResponseAttachment.getFallback());
        assertEquals(jokeValue, commandResponseAttachment.getText());
        assertEquals("[permalink]", commandResponseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=random+joke", commandResponseAttachment
            .getTitleLink());

        verify(slackService, times(1)).getWhitelistedCategories();
        verifyNoMoreInteractions(slackService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "random_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnRandomJokeIfTextIsNull() {
        when(jokeService.randomJokeByCategories(whiteListedCategories)).thenReturn(joke);

        Request request = new Request();
        request.setText(null);
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, commandResponseAttachment.getFallback());
        assertEquals(jokeValue, commandResponseAttachment.getText());
        assertEquals("[permalink]", commandResponseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=random+joke", commandResponseAttachment
            .getTitleLink());

        verify(slackService, times(1)).getWhitelistedCategories();
        verifyNoMoreInteractions(slackService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "random_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnRandomJokeFromACategoryIfTextContainsCategory() {
        String[] categories = new String[]{"dev"};

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(jokeService.randomJokeByCategory("dev")).thenReturn(joke);
        when(slackService.isWhitelistedCategory("dev")).thenReturn(true);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);

        Request request = new Request();
        request.setText("dev");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, commandResponseAttachment.getFallback());
        assertEquals(jokeValue, commandResponseAttachment.getText());
        assertEquals("[permalink]", commandResponseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=random+joke+category", commandResponseAttachment
            .getTitleLink());

        verify(slackService, times(1)).isWhitelistedCategory("dev");
        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(jokeService, times(1)).randomJokeByCategory("dev");
        verifyNoMoreInteractions(jokeService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "random_joke_by_category", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnErrorIfCategoryIsNotWhitelisted() {
        when(slackService.isWhitelistedCategory("explicit")).thenReturn(false);

        Request request = new Request();
        request.setText("explicit");

        SlackCommandResponse response = slackController.command(request);
        assertArrayEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Sorry dude ¯\\_(ツ)_/¯ , the given category (\"explicit\") is not whitelisted. Type `/chuck -cat` to see available categories or search by query `/chuck ? {search_term}`", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(slackService, times(1)).isWhitelistedCategory("explicit");
        verifyNoMoreInteractions(slackService);
    }

    @Test
    public void testReturnErrorIfCategoryDoesNotExist() {
        String[] categories = new String[]{"dev"};

        when(slackService.isWhitelistedCategory("does-not-exist")).thenReturn(true);
        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);

        Request request = new Request();
        request.setText("does-not-exist");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Sorry dude ¯\\_(ツ)_/¯ , we've found no jokes for the given category (\"does-not-exist\"). Type `/chuck -cat` to see available categories or search by query `/chuck ? {search_term}`", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(slackService, times(1)).isWhitelistedCategory("does-not-exist");
        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);
    }

    @Test
    public void testReturnListOfCategories() {
        when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "fashion", "food"});

        Request request = new Request();
        request.setText("-cat");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Available categories are: `dev`, `fashion`, `food`. Type `/chuck {category_name}` to retrieve a random joke from within the given category.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "find_all_categories", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnListOfCategoriesWhitelisted() {
        when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "explicit", "fashion", "food"});

        Request request = new Request();
        request.setText("-cat");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Available categories are: `dev`, `fashion`, `food`. Type `/chuck {category_name}` to retrieve a random joke from within the given category.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "find_all_categories", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnJokeByItsId() {
        when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

        Request request = new Request();
        request.setText(": " + jokeId);
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, commandResponseAttachment.getFallback());
        assertEquals(jokeValue, commandResponseAttachment.getText());
        assertEquals("[permalink]", commandResponseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=joke+by+id", commandResponseAttachment
            .getTitleLink());

        verify(jokeRepository, times(1)).findById(jokeId);
        verifyNoMoreInteractions(jokeRepository);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "find_joke_by_id", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnErrorIfJokeDoesNotExist() {
        when(jokeRepository.findById("does-not-exist")).thenReturn(Optional.empty());

        Request request = new Request();
        request.setText(": does-not-exist");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Sorry dude ¯\\_(ツ)_/¯ , no joke with id (\"does-not-exist\") found.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findById("does-not-exist");
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomPersonalizedJoke() {
        String substitute = "Bob";
        String[] categories = new String[]{"dev", "explicit", "fashion", "food"};

        joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(jokeService.randomPersonalizedJokeByCategories(
            substitute,
            new String[]{"dev", "fashion", "food"}
        )).thenReturn(joke);

        Request request = new Request();
        request.setText("@Bob");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[0];
        assertEquals("Bob programs do not accept input.", commandResponseAttachment.getFallback());
        assertEquals("Bob programs do not accept input.", commandResponseAttachment.getText());
        assertEquals("[permalink]", commandResponseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=random+personalized+joke", commandResponseAttachment
            .getTitleLink());

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(slackService, times(1)).filterNonWhitelistedCategories(
            new String[]{"dev", "explicit", "fashion", "food"}
        );
        verifyNoMoreInteractions(slackService);

        verify(jokeService, times(1)).randomPersonalizedJokeByCategories(
            "Bob",
            new String[]{"dev", "fashion", "food"}
        );
        verifyNoMoreInteractions(jokeService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "random_personalized_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnSearchResultWithLessThanFiveJokes() {
        String query = "Kleenex";
        String[] categories = new String[]{"dev", "movie"};

        Pageable pageable = PageRequest.of(
            0,
            5,
            Sort.unsorted()
        );

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);
        when(jokeService.searchWithCategoryFilter(query, categories, pageable)).thenReturn(
            new PageImpl<>(Arrays.asList(joke, joke, joke))
        );

        Request request = new Request();
        request.setText("? " + query);
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 1 - 3 of 3*.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, commandResponseAttachment.getFallback());
            assertEquals(jokeValue, commandResponseAttachment.getText());
            assertEquals("(" + (i + 1) + ")", commandResponseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=search+joke", commandResponseAttachment
                .getTitleLink());
        }

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);

        verify(jokeService, times(1)).searchWithCategoryFilter(
            query,
            categories,
            pageable
        );
        verifyNoMoreInteractions(jokeService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "search_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnSearchResultWithMoreThanFiveJokes() {
        String query = "Kleenex";
        String[] categories = new String[]{"dev", "movie"};

        Pageable pageable = PageRequest.of(
            0,
            5,
            Sort.unsorted()
        );

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);
        when(jokeService.searchWithCategoryFilter(query, categories, pageable)).thenReturn(
            new PageImpl(
                Arrays.asList(joke, joke, joke, joke, joke),
                pageable,
                6
            )
        );

        Request request = new Request();
        request.setText("? " + query);
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 1 - 5 of 6*. Type `/chuck ? " + query + " --page 2` to see more results.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, commandResponseAttachment.getFallback());
            assertEquals(jokeValue, commandResponseAttachment.getText());
            assertEquals("(" + (i + 1) + ")", commandResponseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=search+joke", commandResponseAttachment
                .getTitleLink());
        }

        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);

        verify(jokeService, times(1)).searchWithCategoryFilter(query, categories, pageable);
        verifyNoMoreInteractions(jokeService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "search_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnSearchResultWithMoreThanFiveJokesSecondPage() {
        String query = "Kleenex";
        String[] categories = new String[]{"dev", "movie"};
        Pageable pageable = PageRequest.of(1, 5, Sort.unsorted());

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);
        when(jokeService.searchWithCategoryFilter(query, categories, pageable)).thenReturn(
            new PageImpl(
                Arrays.asList(joke, joke, joke, joke, joke),
                pageable,
                15
            )
        );

        Request request = new Request();
        request.setText("? " + query + "  --page 2");
        request.setTeamDomain("ACME");

        SlackCommandResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 6 - 10 of 15*. Type `/chuck ? " + query + " --page 3` to see more results.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            SlackCommandResponseAttachment commandResponseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, commandResponseAttachment.getFallback());
            assertEquals(jokeValue, commandResponseAttachment.getText());
            assertEquals("(" + (i + 1 + 5) + ")", commandResponseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term=ACME&utm_campaign=search+joke", commandResponseAttachment
                .getTitleLink());
        }

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);

        verify(jokeService, times(1)).searchWithCategoryFilter(query, categories, pageable);
        verifyNoMoreInteractions(jokeService);

        verify(meterRegistry, times(1)).counter(
            "application_slack_command", "command_type", "search_joke", "team_name", "ACME"
        );
        verifyNoMoreInteractions(meterRegistry);

        verify(counter, times(1)).increment();
        verifyNoMoreInteractions(counter);
    }

    @Test
    public void testReturnErrorIfSearchResultIsEmpty() {
        String query = "poop";
        String[] categories = new String[]{"dev", "movie"};

        Pageable pageable = PageRequest.of(
            0,
            5,
            Sort.unsorted()
        );

        when(jokeRepository.findAllCategories()).thenReturn(categories);
        when(slackService.filterNonWhitelistedCategories(categories)).thenReturn(categories);
        when(jokeService.searchWithCategoryFilter(query, categories, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>())
        );

        Request request = new Request();
        request.setText("? " + query);

        SlackCommandResponse response = slackController.command(request);
        assertArrayEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());

        assertEquals("Your search for *\"" + query + "\"* did not match any joke ¯\\_(ツ)_/¯. Make sure that all words are spelled correctly. Try different keywords. Try more general keywords.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);

        verify(slackService, times(1)).filterNonWhitelistedCategories(categories);
        verifyNoMoreInteractions(slackService);

        verify(jokeService, times(1)).searchWithCategoryFilter(
            query,
            categories,
            pageable
        );
        verifyNoMoreInteractions(jokeService);
    }
}