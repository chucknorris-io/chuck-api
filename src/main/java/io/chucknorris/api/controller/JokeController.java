package io.chucknorris.api.controller;

import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.model.JokeSearchResult;
import io.chucknorris.api.repository.JokeRepository;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "/jokes")
@RestController
@Validated
public class JokeController {

  private JokeRepository jokeRepository;

  /**
   * Returns a new JokeController {@link JokeController} instance.
   */
  public JokeController(JokeRepository jokeRepository) {
    this.jokeRepository = jokeRepository;
  }

  @RequestMapping(
      value = "/categories",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody
  String[] getCategories() {
    return jokeRepository.findAllCategories();
  }

  /**
   * Returns all joke categories delimited by a new line.
   */
  public @ResponseBody
  @RequestMapping(
      value = "/categories",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
  )
  String getCategoryValues() {
    StringBuilder stringBuilder = new StringBuilder();

    for (String category : jokeRepository.findAllCategories()) {
      stringBuilder.append(category + '\n');
    }

    return stringBuilder.toString();
  }

  /**
   * Returns a Joke {@link Joke} by id.
   *
   * @param id The joke id
   * @return joke
   */
  public @ResponseBody
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  Joke getJoke(@PathVariable String id) {
    return jokeRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("Joke with id \"" + id + "\" not found.")
    );
  }

  /**
   * Returns a joke value by id.
   *
   * @param id The joke id
   * @return string
   */
  public @ResponseBody
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
  )
  String getJokeValue(@PathVariable String id, HttpServletResponse response) {
    try {
      return jokeRepository.findById(id).orElseThrow(
          () -> new EntityNotFoundException("Joke with id \"" + id + "\" not found.")
      ).getValue();
    } catch (EntityNotFoundException e) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
      return "";
    }
  }

  /**
   * Returns the model for the joke view.
   */
  public @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
      produces = MediaType.TEXT_HTML_VALUE
  )
  ModelAndView getJokeView(@PathVariable String id) {
    Joke joke = jokeRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("Joke with id \"" + id + "\" not found.")
    );

    String[] ids = jokeRepository.getJokeWindow(id).split(",");

    ModelAndView model = new ModelAndView("joke");
    model.addObject("joke", joke);
    model.addObject("next_joke_url", "/jokes/" + ids[1]);
    model.addObject("current_joke_url", "/jokes/" + id);
    model.addObject("prev_joke_url", "/jokes/" + ids[2]);

    return model;
  }

  /**
   * Returns a random Joke {@link Joke}.
   *
   * @return joke
   */
  public @ResponseBody
  @RequestMapping(
      value = "/random",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  Joke getRandomJoke(
      @RequestParam(value = "category", required = false) String categoryString,
      @RequestParam(value = "name", required = false) String name
  ) {
    if (categoryString == null && name == null) {
      return jokeRepository.getRandomJoke();
    }

    if (categoryString == null && name != null) {
      Joke joke = jokeRepository.getRandomPersonalizedJoke(name);
      if (!(joke instanceof Joke)) {
        throw new EntityNotFoundException("No personalized jokes for name \"" + name + "\" found.");
      }
      return joke;
    }

    String[] availableCategories = jokeRepository.findAllCategories();
    List<String> categories = Arrays.asList(categoryString.split(","));
    for (String category : categories) {
      if (!Arrays.asList(availableCategories).contains(category)) {
        throw new EntityNotFoundException("No jokes for category \"" + category + "\" found.");
      }
    }

    return categories.size() <= 1
        ? jokeRepository.getRandomJokeByCategory(categories.get(0))
        : jokeRepository.getRandomJokeByCategories(categoryString);
  }

  /**
   * Returns a random joke value.
   *
   * @return string
   */
  public @ResponseBody
  @RequestMapping(
      value = "/random",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
  )
  String getRandomJokeValue(
      @RequestParam(value = "category", required = false) final String categoryString,
      @RequestParam(value = "name", required = false) final String name,
      HttpServletResponse response
  ) {
    if (categoryString == null && name == null) {
      return jokeRepository.getRandomJoke().getValue();
    }

    if (categoryString == null && name != null) {
      Joke joke = jokeRepository.getRandomPersonalizedJoke(name);
      if (!(joke instanceof Joke)) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "";
      }
      return joke.getValue();
    }

    String[] availableCategories = jokeRepository.findAllCategories();
    List<String> categories = Arrays.asList(categoryString.split(","));
    for (String category : categories) {
      if (!Arrays.asList(availableCategories).contains(category)) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "";
      }
    }

    return categories.size() <= 1
        ? jokeRepository.getRandomJokeByCategory(categories.get(0)).getValue()
        : jokeRepository.getRandomJokeByCategories(categoryString).getValue();
  }

  /**
   * Returns a JokeSearchResult {@link JokeSearchResult}.
   *
   * @param query The search query
   * @return jokeSearchResult
   */
  public @ResponseBody
  @RequestMapping(
      value = "/search",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  JokeSearchResult search(
      @RequestParam(value = "query")
      @Size(min = 3, max = 120) final String query
  ) {
    Joke[] jokes = jokeRepository.searchByQuery(query);
    return new JokeSearchResult(jokes);
  }

  /**
   * Returns a search result delimited by a new line.
   */
  public @ResponseBody
  @RequestMapping(
      value = "/search",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
  )
  String searchValues(
      @RequestParam(value = "query")
      @Size(min = 3, max = 120) final String query
  ) {
    Joke[] jokes = jokeRepository.searchByQuery(query);
    StringBuilder stringBuilder = new StringBuilder();

    for (Joke joke : jokes) {
      stringBuilder.append(joke.getValue() + '\n');
    }

    return stringBuilder.toString();
  }
}
