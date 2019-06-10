package io.chucknorris.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.model.JokeSearchResult;
import io.chucknorris.api.repository.JokeRepository;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@RunWith(MockitoJUnitRunner.class)
public class JokeControllerTest {

  private static String jokeId, jokeValue;
  private static Joke joke;

  @InjectMocks
  private JokeController jokeController;

  @Mock
  private JokeRepository jokeRepository;

  @Mock
  private MockHttpServletResponse httpServletResponse;

  @Before
  public void setUp() {
    jokeId = "ys--0t_-rrifz5jtcparbg";
    jokeValue = "Some people ask for a Kleenex when they sneeze, Chuck Norris asks for a body bag.";
    joke = new Joke()
        .setCategories(new String[]{"dev"})
        .setId(jokeId)
        .setValue(jokeValue);
  }

  @Test
  public void testGetCategories() {
    when(jokeRepository.findAllCategories()).thenReturn(
        new String[]{"dev", "animal"}
    );

    String[] categories = jokeController.getCategories();
    assertEquals("dev", categories[0]);
    assertEquals("animal", categories[1]);
    assertEquals(2, categories.length);

    verify(jokeRepository, times(1)).findAllCategories();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetCategoryValues() {
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "animal"});

    String categoryValues = jokeController.getCategoryValues();
    assertEquals("dev\nanimal\n", categoryValues);

    verify(jokeRepository, times(1)).findAllCategories();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetJokeReturnsJoke() {
    when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

    Joke joke = jokeController.getJoke(jokeId);
    assertEquals(JokeControllerTest.joke, joke);

    verify(jokeRepository, times(1)).findById(jokeId);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test(expected = EntityNotFoundException.class)
  public void testGetJokeThrowsException() {
    when(jokeRepository.findById("does-not-exist")).thenThrow(new EntityNotFoundException(""));

    jokeController.getJoke("does-not-exist");

    verify(jokeRepository, times(1)).findById("does-not-exist");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetJokeValueReturnsJokeValue() {
    when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

    String jokeValue = jokeController.getJokeValue(jokeId, this.httpServletResponse);
    assertEquals(joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).findById(jokeId);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetJokeValueReturnsEmptyStringIfEntityNotFound() {
    when(jokeRepository.findById("does-not-exist")).thenThrow(new EntityNotFoundException(""));

    String jokeValue = jokeController.getJokeValue("does-not-exist", this.httpServletResponse);
    assertEquals("", jokeValue);

    verify(jokeRepository, times(1)).findById("does-not-exist");
    verify(this.httpServletResponse).setStatus(404);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void getJokeViewReturnsModelAndView() {
    when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));
    when(jokeRepository.getJokeWindow(jokeId)).thenReturn(
        jokeId + ',' + "yvrhbpauspegla4pf7dxna" + ',' + "id4dTcDiRneK4btgOGpNNw"
    );

    ModelAndView view = jokeController.getJokeView(jokeId);
    assertEquals(joke, view.getModel().get("joke"));
    assertEquals("/jokes/yvrhbpauspegla4pf7dxna", view.getModel().get("next_joke_url"));
    assertEquals("/jokes/ys--0t_-rrifz5jtcparbg", view.getModel().get("current_joke_url"));
    assertEquals("/jokes/id4dTcDiRneK4btgOGpNNw", view.getModel().get("prev_joke_url"));

    verify(jokeRepository, times(1)).findById(jokeId);
    verify(jokeRepository, times(1)).getJokeWindow(jokeId);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJoke() {
    when(jokeRepository.getRandomJoke()).thenReturn(joke);

    Joke joke = jokeController.getRandomJoke(null, null);
    assertEquals(JokeControllerTest.joke, joke);

    verify(jokeRepository, times(1)).getRandomJoke();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJokeByCategory() {
    when(jokeRepository.getRandomJokeByCategory("dev")).thenReturn(joke);
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev"});

    Joke joke = jokeController.getRandomJoke("dev", null);
    assertEquals(JokeControllerTest.joke, joke);

    verify(jokeRepository, times(1)).findAllCategories();
    verify(jokeRepository, times(1)).getRandomJokeByCategory("dev");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test(expected = EntityNotFoundException.class)
  public void testGetRandomJokeReturnsJokeByCategoryThrowsException() {
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{});

    jokeController.getRandomJoke("dev", null);

    verify(jokeRepository, times(1)).findAllCategories();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJokeByMultipleCategories() {
    when(jokeRepository.getRandomJokeByCategories("dev,movie")).thenReturn(joke);
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "movie"});

    Joke joke = jokeController.getRandomJoke("dev,movie", null);
    assertEquals(JokeControllerTest.joke, joke);

    verify(jokeRepository, times(1)).findAllCategories();
    verify(jokeRepository, times(1)).getRandomJokeByCategories(
        "dev,movie"
    );
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test(expected = EntityNotFoundException.class)
  public void testGetRandomJokeReturnsJokeByMultipleCategoriesThrowsException() {
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{});

    jokeController.getRandomJoke("dev,does-not-exist", null);

    verify(jokeRepository, times(1)).findAllCategories();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomPersonalisedJokeReturnsJoke() {
    joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));
    when(jokeRepository.getRandomPersonalizedJoke("Bob")).thenReturn(joke);

    Joke joke = jokeController.getRandomJoke(null, "Bob");
    assertEquals(JokeControllerTest.joke, joke);

    verify(jokeRepository, times(1)).getRandomPersonalizedJoke("Bob");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test(expected = EntityNotFoundException.class)
  public void testGetRandomPersonalisedJokeThrowsException() {
    joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));
    when(jokeRepository.getRandomPersonalizedJoke("Bob")).thenReturn(null);

    jokeController.getRandomJoke(null, "Bob");

    verify(jokeRepository, times(1)).getRandomPersonalizedJoke("Bob");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJokeValue() {
    when(jokeRepository.getRandomJoke()).thenReturn(joke);

    String jokeValue = jokeController.getRandomJokeValue(
        null, null, this.httpServletResponse
    );
    assertEquals(joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).getRandomJoke();
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJokeValueByCategory() {
    when(jokeRepository.getRandomJokeByCategory("dev")).thenReturn(joke);
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev"});

    String jokeValue = jokeController.getRandomJokeValue(
        "dev", null, this.httpServletResponse
    );
    assertEquals(joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).findAllCategories();
    verify(jokeRepository, times(1)).getRandomJokeByCategory("dev");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeReturnsJokeValueByMultipleCategories() {
    when(jokeRepository.getRandomJokeByCategories("dev,movie")).thenReturn(joke);
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{"dev", "movie"});

    String jokeValue = jokeController.getRandomJokeValue(
        "dev,movie", null, this.httpServletResponse
    );
    assertEquals(joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).findAllCategories();
    verify(jokeRepository, times(1)).getRandomJokeByCategories(
        "dev,movie"
    );
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomJokeValueReturnsEmptyStringIfCategoryDoesNotExist() {
    when(jokeRepository.findAllCategories()).thenReturn(new String[]{});

    String jokeValue = jokeController.getRandomJokeValue(
        "does-not-exist", null, this.httpServletResponse
    );
    assertEquals("", jokeValue);

    verify(jokeRepository, times(1)).findAllCategories();
    verify(this.httpServletResponse).setStatus(404);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomPersonalisedJokeValueReturnsJokeValue() {
    joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));
    when(jokeRepository.getRandomPersonalizedJoke("Bob")).thenReturn(joke);

    String jokeValue = jokeController.getRandomJokeValue(
        null, "Bob", this.httpServletResponse
    );
    assertEquals(joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).getRandomPersonalizedJoke("Bob");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetRandomPersonalisedJokeValueEmptyStringIfNoJokeWasFound() {
    joke.setValue(joke.getValue().replace("Chuck Norris", "Bob"));
    when(jokeRepository.getRandomPersonalizedJoke("Bob")).thenReturn(null);

    String jokeValue = jokeController.getRandomJokeValue(
        null, "Bob", this.httpServletResponse
    );
    assertEquals("", jokeValue);

    verify(jokeRepository, times(1)).getRandomPersonalizedJoke("Bob");
    verify(this.httpServletResponse).setStatus(404);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testSearch() {
    when(jokeRepository.searchByQuery("Kleenex")).thenReturn(new Joke[]{joke});

    JokeSearchResult jokeSearchResult = jokeController.search("Kleenex");
    assertEquals(jokeSearchResult.getTotal(), 1);
    assertSame(jokeSearchResult.getResult()[0], joke);

    verify(jokeRepository, times(1)).searchByQuery("Kleenex");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testSearchValues() {
    when(jokeRepository.searchByQuery("Kleenex")).thenReturn(new Joke[]{joke});

    String searchValues = jokeController.searchValues("Kleenex");
    assertEquals(
        "Some people ask for a Kleenex when they sneeze, Chuck Norris asks for a body bag.\n",
        searchValues);

    verify(jokeRepository, times(1)).searchByQuery("Kleenex");
    verifyNoMoreInteractions(jokeRepository);
  }
}