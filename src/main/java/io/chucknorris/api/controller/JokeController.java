package io.chucknorris.api.controller;

import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/jokes")
public class JokeController {

  @Autowired
  private JokeRepository jokeRepository;

  @Autowired
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
}
