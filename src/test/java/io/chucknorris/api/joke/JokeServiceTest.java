package io.chucknorris.api.joke;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(MockitoJUnitRunner.class)
public class JokeServiceTest {

  private static String jokeId, jokeValue;
  private static Joke joke;

  @Mock
  private JokeRepository jokeRepository;

  @InjectMocks
  private JokeService jokeService;

  @Before
  public void setUp() throws Exception {
    jokeId = "ys--0t_-rrifz5jtcparbg";
    jokeValue = "Some people ask for a Kleenex when they sneeze, Chuck Norris asks for a body bag.";
    joke = new Joke()
        .setCategories(new String[]{"dev"})
        .setId(jokeId)
        .setValue(jokeValue);
  }

  @Test
  public void testRandomJokeByCategoriesReturnsJoke() {
    when(jokeRepository.getRandomJokeByCategories("dev,movie")).thenReturn(joke);

    Joke joke = jokeService.randomJokeByCategories(new String[]{"dev", "movie"});
    assertEquals(JokeServiceTest.joke, joke);

    verify(jokeRepository, times(1)).getRandomJokeByCategories("dev,movie");
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testRandomPersonalizedJokeByCategoriesReturnsJoke() {
    String substitute = "Bob";
    String[] categories = new String[]{"dev", "movie"};

    joke.setValue(joke.getValue().replace("Chuck Norris", substitute));
    when(
        jokeRepository.getRandomPersonalizedJokeByCategories(
            substitute,
            "dev,movie"
        )
    ).thenReturn(joke);

    Joke joke = jokeService.randomPersonalizedJokeByCategories(substitute, categories);
    assertEquals(JokeServiceTest.joke, joke);

    verify(jokeRepository, times(1)).getRandomPersonalizedJokeByCategories(
        substitute,
        "dev,movie"
    );
    verifyNoMoreInteractions(jokeRepository);
  }

  @Test
  public void testSearchWithCategoryFilter() {
    String query = "Kleenex";
    String[] categories = new String[]{"dev", "movie"};
    Pageable pageable = PageRequest.of(1, 5);

    when(
        jokeRepository.findByValueContainsAndFilter(query, "dev,movie", pageable)
    ).thenReturn(Page.empty());

    jokeService.searchWithCategoryFilter(query, categories, pageable);

    verify(jokeRepository, times(1)).findByValueContainsAndFilter(
        query,
        "dev,movie",
        pageable
    );
    verifyNoMoreInteractions(jokeRepository);
  }
}