package io.chucknorris.api.joke;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JokeService {
  private JokeRepository jokeRepository;

  public JokeService(JokeRepository jokeRepository) {
    this.jokeRepository = jokeRepository;
  }

  /**
   * Returns a random joke filtered by a given category.
   */
  public Joke randomJokeByCategory(final String category) {
    return jokeRepository.getRandomJokeByCategory(category);
  }

  /**
   * Returns a random joke filtered by a given array of categories.
   */
  public Joke randomJokeByCategories(final String[] categories) {
    return jokeRepository.getRandomJokeByCategories(
        String.join(",", categories)
    );
  }

  /**
   * Returns a random personalized joke filtered by a given array of categories.
   */
  public Joke randomPersonalizedJokeByCategories(
      final String substitute,
      final String[] categories
  ) {
    return jokeRepository.getRandomPersonalizedJokeByCategories(
        substitute,
        String.join(",", categories)
    );
  }

  /**
   * Search jokes by query and with category filter.
   */
  public Page<Joke> searchWithCategoryFilter(
      final String query,
      final String[] categories,
      final Pageable pageable
  ) {
    return jokeRepository.findByValueContainsAndFilter(
        query,
        String.join(",", categories),
        pageable
    );
  }
}
