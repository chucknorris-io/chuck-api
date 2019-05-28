package io.chucknorris.api.controller;

import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.model.JokeSearchResult;
import io.chucknorris.api.repository.JokeRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/jokes")
@Validated
public class JokeController {
  private JokeRepository jokeRepository;

  public JokeController(JokeRepository jokeRepository) {
    this.jokeRepository = jokeRepository;
  }

  @RequestMapping(
          value = "/categories",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody String[] getCategories() {
    return jokeRepository.findAllCategories();
  }

  @RequestMapping(
          value = "/categories",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
          produces = MediaType.TEXT_PLAIN_VALUE
  )
  public @ResponseBody
  String getCategoryValues() {
    StringBuilder stringBuilder = new StringBuilder();

    for (String category : jokeRepository.findAllCategories()) {
      stringBuilder.append(category + '\n');
    }

    return stringBuilder.toString();
  }

  @RequestMapping(
          value = "/{id}",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody Joke getJoke(@PathVariable String id) {
    return jokeRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Joke with id \"" + id + "\" not found.")
    );
  }

  @RequestMapping(
          value = "/{id}",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
          produces = MediaType.TEXT_PLAIN_VALUE
  )
  public @ResponseBody String getJokeValue(@PathVariable String id, HttpServletResponse response) {
    try {
      return jokeRepository.findById(id).orElseThrow(
              () -> new EntityNotFoundException("Joke with id \"" + id + "\" not found.")
      ).getValue();
    } catch(EntityNotFoundException e) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
      return "";
    }
  }

  @RequestMapping(
          value = "/{id}",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
          produces = MediaType.TEXT_HTML_VALUE
  )
  public ModelAndView getJokeView(@PathVariable String id) {
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

  @RequestMapping(
          value = "/random",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody
  Joke getRandomJoke(@RequestParam(value = "category", required = false) final String category) {
    if (category == null) {
      return jokeRepository.getRandomJoke();
    }

    String[] categories = jokeRepository.findAllCategories();
    if(!Arrays.asList(categories).contains(category)) {
      throw new EntityNotFoundException("No jokes for category \"" + category + "\" found.");
    } else {
      return jokeRepository.getRandomJokeByCategory(category);
    }
  }

  @RequestMapping(
          value = "/random",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
          produces = MediaType.TEXT_PLAIN_VALUE
  )
  public @ResponseBody
  String getRandomJokeValue(@RequestParam(value = "category", required = false) final String category, HttpServletResponse response) {
    if (category == null) {
      return jokeRepository.getRandomJoke().getValue();
    }

    String[] categories = jokeRepository.findAllCategories();
    if(!Arrays.asList(categories).contains(category)) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
      return "";
    } else {
      return jokeRepository.getRandomJokeByCategory(category).getValue();
    }
  }

  @RequestMapping(
          value = "/search",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody
  JokeSearchResult search(
          @RequestParam(value = "query")
          @Size(min = 3, max = 120) final String query
  ) {
    Joke[] jokes = jokeRepository.searchByQuery(query);
    return new JokeSearchResult(jokes);
  }

  @RequestMapping(
          value = "/search",
          method = RequestMethod.GET,
          headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_PLAIN_VALUE,
          produces = MediaType.TEXT_PLAIN_VALUE
  )
  public @ResponseBody
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
