package io.chucknorris.api.lib.dailychuck;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;
import io.chucknorris.api.exception.EntityNotFoundException;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DailyChuckRss extends AbstractRssFeedView {

    private String baseUrl;
    private DailyChuck dailyChuck;
    private JokeRepository jokeRepository;

    public DailyChuckRss(String baseUrl, DailyChuck dailyChuck, JokeRepository jokeRepository) {
        this.baseUrl = baseUrl;
        this.dailyChuck = dailyChuck;
        this.jokeRepository = jokeRepository;
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
        feed.setTitle("The Daily Chuck");
        feed.setDescription("Get your daily dose of the best #ChuckNorrisFacts every morning straight into your inbox.");
        feed.setLink("https://" + baseUrl + "/feed/daily-chuck.xml");
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        DailyChuckIssue[] dailyChuckIssues = dailyChuck.getIssues();
        Arrays.sort(dailyChuck.getIssues(), Comparator.comparing(DailyChuckIssue::getDate).reversed());

        DailyChuckIssue currentIssue = dailyChuckIssues[0];
        Joke joke = jokeRepository.findById(currentIssue.getJokeId()).orElseThrow(
            () -> new EntityNotFoundException("Joke with id \"" + currentIssue.getJokeId() + "\" not found.")
        );

        Long issueNumber = dailyChuck.getIssueNumber();
        Item entry = new Item();
        entry.setTitle(issueNumber.toString());
        entry.setLink("https://" + baseUrl + joke.getUrl());
        entry.setPubDate(currentIssue.getDate());

        Content content = new Content();
        content.setValue(joke.getValue());
        entry.setContent(content);

        return Arrays.asList(entry);
    }
}
