package io.chucknorris.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.chucknorris.api.lib.event.EventService;
import io.chucknorris.api.lib.slack.Impl.*;
import io.chucknorris.api.lib.slack.SlackResponse;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SlackControllerTest {

    private static String iconUrl, jokeId, jokeValue;
    private static Joke joke;

    @Mock
    private EventService eventService;

    @Mock
    private JokeRepository jokeRepository;

    @InjectMocks
    private SlackController slackController;

    @Mock
    private SlackService slackService;

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
    }

    @Test
    public void testConnect() throws JsonProcessingException {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken("23BE2D81-35B6-4B73-BCC9-8B6731D2540E");

        when(slackService.requestAccessToken("my-super-secret-code")).thenReturn(accessToken);
        when(eventService.publishEvent(any())).thenReturn(any());

        ModelAndView view = slackController.connect("my-super-secret-code");
        assertEquals(HttpStatus.OK, view.getStatus());
        assertEquals("Congrats, the app was successfully installed for your Slack team!", view.getModel().get("page_title"));
        assertEquals(false, view.getModel().get("error"));
        assertEquals(null, view.getModel().get("message"));

        verify(slackService, times(1)).requestAccessToken("my-super-secret-code");
        verifyNoMoreInteractions(slackService);

        verify(eventService, times(1)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
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

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Available commands:*", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        ResponseAttachment newsletter = response.getAttachments()[0];
        assertEquals(null, newsletter.getFallback());
        assertEquals(":facepunch: Sign up for *The Daily Chuck* and get your daily dose of the best #ChuckNorrisFacts every morning straight int your inbox! https://mailchi.mp/5a19a2898bf7/the-daily-chuck", newsletter.getText());
        assertEquals("The Daily Chuck", newsletter.getTitle());
        assertEquals(null, newsletter.getTitleLink());
        assertEquals(new String[]{"text"}, newsletter.getMrkdownIn());

        ResponseAttachment randomJoke = response.getAttachments()[1];
        assertEquals(null, randomJoke.getFallback());
        assertEquals("Type `/chuck` to get a random joke.", randomJoke.getText());
        assertEquals("Random joke", randomJoke.getTitle());
        assertEquals(null, randomJoke.getTitleLink());
        assertEquals(new String[]{"text"}, randomJoke.getMrkdownIn());

        ResponseAttachment search = response.getAttachments()[2];
        assertEquals(null, search.getFallback());
        assertEquals("Type `/chuck ? {search_term}` to search within tens of thousands Chuck Norris jokes.", search.getText());
        assertEquals("Free text search", search.getTitle());
        assertEquals(null, search.getTitleLink());
        assertEquals(new String[]{"text"}, search.getMrkdownIn());

        ResponseAttachment randomJokePersonalized = response.getAttachments()[3];
        assertEquals(null, randomJokePersonalized.getFallback());
        assertEquals("Type `/chuck @ {user_name}` to get a random personalized joke.", randomJokePersonalized.getText());
        assertEquals("Random personalized joke", randomJokePersonalized.getTitle());
        assertEquals(null, randomJokePersonalized.getTitleLink());
        assertEquals(new String[]{"text"}, randomJokePersonalized.getMrkdownIn());

        ResponseAttachment randomJokeFromCategory = response.getAttachments()[4];
        assertEquals(null, randomJokeFromCategory.getFallback());
        assertEquals("Type `/chuck {category_name}` to get a random joke from within a given category.", randomJokeFromCategory.getText());
        assertEquals("Random joke from category", randomJokeFromCategory.getTitle());
        assertEquals(null, randomJokeFromCategory.getTitleLink());
        assertEquals(new String[]{"text"}, randomJokeFromCategory.getMrkdownIn());

        ResponseAttachment categories = response.getAttachments()[5];
        assertEquals(null, categories.getFallback());
        assertEquals("Type `/chuck -cat` to retrieve a list of all categories.", categories.getText());
        assertEquals("Categories", categories.getTitle());
        assertEquals(null, categories.getTitleLink());
        assertEquals(new String[]{"text"}, categories.getMrkdownIn());

        ResponseAttachment help = response.getAttachments()[6];
        assertEquals(null, help.getFallback());
        assertEquals("Type `/chuck : {joke_id}` to retrieve get a joke by a given `id`.", help.getText());
        assertEquals("Get joke by id", help.getTitle());
        assertEquals(null, help.getTitleLink());
        assertEquals(new String[]{"text"}, help.getMrkdownIn());

        ResponseAttachment jokeById = response.getAttachments()[7];
        assertEquals(null, jokeById.getFallback());
        assertEquals("Type `/chuck help` to display a list of available commands.", jokeById.getText());
        assertEquals("Help", jokeById.getTitle());
        assertEquals(null, jokeById.getTitleLink());
        assertEquals(new String[]{"text"}, jokeById.getMrkdownIn());

        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomJokeIfTextIsEmpty() {
        when(jokeRepository.getRandomJoke()).thenReturn(joke);

        Request request = new Request();
        request.setText("");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        ResponseAttachment responseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, responseAttachment.getFallback());
        assertEquals(jokeValue, responseAttachment.getText());
        assertEquals("[permalink]", responseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=random+joke", responseAttachment.getTitleLink());

        verify(jokeRepository, times(1)).getRandomJoke();
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomJokeIfTextIsNull() {
        when(jokeRepository.getRandomJoke()).thenReturn(joke);

        Request request = new Request();

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        ResponseAttachment responseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, responseAttachment.getFallback());
        assertEquals(jokeValue, responseAttachment.getText());
        assertEquals("[permalink]", responseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=random+joke", responseAttachment.getTitleLink());

        verify(jokeRepository, times(1)).getRandomJoke();
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomJokeFromACategoryIfTextContainsCategory() {
        when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev"});
        when(jokeRepository.getRandomJokeByCategory("dev")).thenReturn(joke);

        Request request = new Request();
        request.setText("dev");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        ResponseAttachment responseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, responseAttachment.getFallback());
        assertEquals(jokeValue, responseAttachment.getText());
        assertEquals("[permalink]", responseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=random+joke+category", responseAttachment.getTitleLink());

        verify(jokeRepository, times(1)).findAllCategories();
        verify(jokeRepository, times(1)).getRandomJokeByCategory("dev");
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnErrorIfCategoryDoesNotExist() {
        when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev"});

        Request request = new Request();
        request.setText("does-not-exist");

        SlackResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Sorry dude ¯\\_(ツ)_/¯ , we've found no jokes for the given category (\"does-not-exist\"). Type `/chuck -cat` to see available categories or search by query `/chuck ? {search_term}`", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findAllCategories();
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnListOfCategories() {
        when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "fashion", "food"});

        Request request = new Request();
        request.setText("-cat");

        SlackResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Available categories are: `dev`, `fashion`, `food`. Type `/chuck {category_name}` to retrieve a random joke from within the given category.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());
    }

    @Test
    public void testReturnJokeByItsId() {
        when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

        Request request = new Request();
        request.setText(": " + jokeId);

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        ResponseAttachment responseAttachment = response.getAttachments()[0];
        assertEquals(jokeValue, responseAttachment.getFallback());
        assertEquals(jokeValue, responseAttachment.getText());
        assertEquals("[permalink]", responseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=joke+by+id", responseAttachment.getTitleLink());

        verify(jokeRepository, times(1)).findById(jokeId);
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnErrorIfJokeDoesNotExist() {
        when(jokeRepository.findById("does-not-exist")).thenReturn(Optional.empty());

        Request request = new Request();
        request.setText(": does-not-exist");

        SlackResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("Sorry dude ¯\\_(ツ)_/¯ , no joke with id (\"does-not-exist\") found.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findById("does-not-exist");
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnRandomPersonalizedJoke() {
        joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));
        when(jokeRepository.getRandomPersonalizedJoke("Bob")).thenReturn(joke);

        Request request = new Request();
        request.setText("@Bob");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals(null, response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        ResponseAttachment responseAttachment = response.getAttachments()[0];
        assertEquals("Bob programs do not accept input.", responseAttachment.getFallback());
        assertEquals("Bob programs do not accept input.", responseAttachment.getText());
        assertEquals("[permalink]", responseAttachment.getTitle());
        assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=random+personalized+joke", responseAttachment.getTitleLink());

        verify(jokeRepository, times(1)).getRandomPersonalizedJoke("Bob");
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnSearchResultWithLessThanFiveJokes() {
        Pageable pageable = PageRequest.of(0, 5);
        when(jokeRepository.findByValueContains("program", pageable)).thenReturn(
                new PageImpl<>(Arrays.asList(joke, joke, joke))
        );

        Request request = new Request();
        request.setText("? program");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 1 - 3 of 3*.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            ResponseAttachment responseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, responseAttachment.getFallback());
            assertEquals(jokeValue, responseAttachment.getText());
            assertEquals("(" + (i + 1) + ")", responseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=search+joke", responseAttachment.getTitleLink());
        }

        verify(jokeRepository, times(1)).findByValueContains("program", pageable);
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnSearchResultWithMoreThanFiveJokes() {
        Pageable pageable = PageRequest.of(0, 5);
        Page page = new PageImpl(
                Arrays.asList(joke, joke, joke, joke, joke),
                pageable,
                6
        );
        when(jokeRepository.findByValueContains("program", pageable)).thenReturn(page);

        Request request = new Request();
        request.setText("? program");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 1 - 5 of 6*. Type `/chuck ? program --page 2` to see more results.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            ResponseAttachment responseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, responseAttachment.getFallback());
            assertEquals(jokeValue, responseAttachment.getText());
            assertEquals("(" + (i + 1) + ")", responseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=search+joke", responseAttachment.getTitleLink());
        }

        verify(jokeRepository, times(1)).findByValueContains("program", pageable);
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnSearchResultWithMoreThanFiveJokesSecondPage() {
        Pageable pageable = PageRequest.of(1, 5);
        Page page = new PageImpl(
                Arrays.asList(joke, joke, joke, joke, joke),
                pageable,
                15
        );
        when(jokeRepository.findByValueContains("program", pageable)).thenReturn(page);

        Request request = new Request();
        request.setText("? program  --page 2");

        SlackResponse response = slackController.command(request);
        assertEquals(iconUrl, response.getIconUrl());
        assertEquals("*Search results: 6 - 10 of 15*. Type `/chuck ? program --page 3` to see more results.", response.getText());
        assertEquals(ResponseType.IN_CHANNEL, response.getResponseType());

        for (int i = 0; i < response.getAttachments().length; i++) {
            ResponseAttachment responseAttachment = response.getAttachments()[i];
            assertEquals(jokeValue, responseAttachment.getFallback());
            assertEquals(jokeValue, responseAttachment.getText());
            assertEquals("(" + (i + 1 + 5) + ")", responseAttachment.getTitle());
            assertEquals("https://localhost/jokes/bg_h3xursougaxzprcrl0q?utm_source=slack&utm_medium=api&utm_term&utm_campaign=search+joke", responseAttachment.getTitleLink());
        }

        verify(jokeRepository, times(1)).findByValueContains("program", pageable);
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void testReturnErrorIfSearchResultIsEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        when(jokeRepository.findByValueContains("poop", pageable)).thenReturn(
                new PageImpl<>(new ArrayList<>())
        );

        Request request = new Request();
        request.setText("? poop");

        SlackResponse response = slackController.command(request);
        assertEquals(null, response.getAttachments());
        assertEquals(iconUrl, response.getIconUrl());

        assertEquals("Your search for *\"poop\"* did not match any joke ¯\\_(ツ)_/¯. Make sure that all words are spelled correctly. Try different keywords. Try more general keywords.", response.getText());
        assertEquals(ResponseType.EPHEMERAL, response.getResponseType());

        verify(jokeRepository, times(1)).findByValueContains("poop", pageable);
        verifyNoMoreInteractions(jokeRepository);
    }
}