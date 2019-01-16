package io.chucknorris.api.controller;

import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JokeControllerTest {

  private static String jokeId = "ys--0t_-rrifz5jtcparbg";
  private static String jokeValue = "Some people ask for a Kleenex when they sneeze, Chuck Norris asks for a body bag.";
  private static Joke joke = new Joke()
      .setId(jokeId)
      .setValue(jokeValue);

  @InjectMocks
  private JokeController jokeController;

  @Mock
  private JokeRepository jokeRepository;

  @Mock
  private MockHttpServletResponse httpServletResponse;

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
  public void testGetJokeReturnsJoke() {
    when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

    Joke joke = jokeController.getJoke(jokeId);
    assertEquals(this.joke, joke);

    verify(jokeRepository, times(1)).findById(jokeId);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test(expected = EntityNotFoundException.class)
  public void testGetJokeThrowsException() {
    when(jokeRepository.findById("does-not-exist")).thenThrow(
            new EntityNotFoundException("")
    );

    jokeController.getJoke("does-not-exist");

    verify(jokeRepository, times(1)).findById("does-not-exist");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetJokeValueReturnsJokeValue() {
    when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(joke));

    String jokeValue = jokeController.getJokeValue(jokeId, this.httpServletResponse);
    assertEquals(this.joke.getValue(), jokeValue);

    verify(jokeRepository, times(1)).findById(jokeId);
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testGetJokeValueReturnsEmptyStringIfEnityNotFound() {
    when(jokeRepository.findById("does-not-exist")).thenThrow(
            new EntityNotFoundException("")
    );

    String jokeValue = jokeController.getJokeValue("does-not-exist", this.httpServletResponse);
    assertEquals("", jokeValue);

    verify(jokeRepository, times(1)).findById("does-not-exist");
    verify(this.httpServletResponse).setStatus(404);
    verifyNoMoreInteractions(jokeRepository);
  }
}