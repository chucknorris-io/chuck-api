package io.chucknorris.api.controller;

import io.chucknorris.api.lib.DateUtil;
import io.chucknorris.api.lib.dailychuck.DailyChuck;
import io.chucknorris.api.lib.dailychuck.DailyChuckIssue;
import io.chucknorris.api.lib.dailychuck.DailyChuckPublishedEvent;
import io.chucknorris.api.lib.dailychuck.DailyChuckService;
import io.chucknorris.api.lib.event.EventService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.util.Date;

@RestController
public class FeedController {

    DailyChuckService dailyChuckService;
    DateUtil dateUtil;
    EventService eventService;

    public FeedController(DailyChuckService dailyChuckService, DateUtil dateUtil, EventService eventService) {
        this.dailyChuckService = dailyChuckService;
        this.dateUtil = dateUtil;
        this.eventService = eventService;
    }

    @RequestMapping(
        value = {"/feed/daily-chuck.json", "/feed/daily-chuck"},
        method = RequestMethod.GET,
        headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DailyChuck dailyChuckJson() throws IOException {
        DailyChuck dailyChuck = dailyChuckService.getDailyChuck();

        Date now = dateUtil.now();
        if (dailyChuck.findIssueByDate(now) instanceof DailyChuckIssue) {
            return dailyChuck;
        }

        DailyChuckIssue dailyChuckIssue = dailyChuckService.composeDailyChuckIssue(dailyChuck.getIssues());
        dailyChuck.addIssue(dailyChuckIssue);

        dailyChuckService.persist(dailyChuck);

        eventService.publishEvent(new DailyChuckPublishedEvent(dailyChuckIssue));

        return dailyChuck;
    }

    @RequestMapping(
        value = {"/feed/daily-chuck.xml", "/feed/daily-chuck"},
        method = RequestMethod.GET,
        headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_XML_VALUE,
        produces = MediaType.APPLICATION_RSS_XML_VALUE
    )
    public View dailyChuckRss() throws IOException {
        DailyChuck dailyChuck = dailyChuckService.getDailyChuck();

        Date now = dateUtil.now();
        if (dailyChuck.findIssueByDate(now) instanceof DailyChuckIssue) {
            return dailyChuckService.toRss(dailyChuck);
        }

        DailyChuckIssue dailyChuckIssue = dailyChuckService.composeDailyChuckIssue(dailyChuck.getIssues());
        dailyChuck.addIssue(dailyChuckIssue);

        dailyChuckService.persist(dailyChuck);

        eventService.publishEvent(new DailyChuckPublishedEvent(dailyChuckIssue));

        return dailyChuckService.toRss(dailyChuck);
    }
}
