package io.chucknorris.api.controller;

import io.chucknorris.api.lib.slack.Impl.SlackService;
import io.chucknorris.api.repository.JokeRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private JokeRepository jokeRepository;
    private SlackService slackService;

    public HomeController(JokeRepository jokeRepository, SlackService slackService) {
        this.jokeRepository = jokeRepository;
        this.slackService = slackService;
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView view() {
        ModelAndView model = new ModelAndView("home");
        model.addObject("joke", jokeRepository.getRandomJoke());
        model.addObject("slack_authorize_url", slackService.composeAuthorizeUri());

        return model;
    }
}
