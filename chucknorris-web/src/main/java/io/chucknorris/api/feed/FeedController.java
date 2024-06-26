package io.chucknorris.api.feed;

import io.chucknorris.api.feed.dailychuck.DailyChuck;
import io.chucknorris.api.feed.dailychuck.DailyChuckIssue;
import io.chucknorris.api.feed.dailychuck.DailyChuckPublishedEvent;
import io.chucknorris.api.feed.dailychuck.DailyChuckService;
import io.chucknorris.lib.DateUtil;
import io.chucknorris.lib.event.EventService;
import io.chucknorris.lib.mailchimp.MailchimpService;
import io.chucknorris.lib.mailchimp.MailingListStatistic;
import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
public class FeedController {

    private DailyChuckService dailyChuckService;
    private DateUtil dateUtil;
    private EventService eventService;
    private MailchimpService mailchimpService;

    @Value("${mailchimp.dailychuck.list_id}")
    private String dailyChuckListId;

    /** Returns a new FeedController {@link FeedController} instance. */
    public FeedController(
            DailyChuckService dailyChuckService,
            DateUtil dateUtil,
            EventService eventService,
            MailchimpService mailchimpService) {
        this.dailyChuckService = dailyChuckService;
        this.dateUtil = dateUtil;
        this.eventService = eventService;
        this.mailchimpService = mailchimpService;
    }

    /**
     * Returns a new DailyChuck {@link DailyChuck} instance.
     *
     * @return dailyChuck
     * @throws IOException Thrown if {@link DailyChuck} can't ber persisted.
     */
    public @RequestMapping(value = { "/feed/daily-chuck.json", "/feed/daily-chuck" }, method = RequestMethod.GET, headers = HttpHeaders.ACCEPT + "="
            + MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) DailyChuck dailyChuckJson() throws IOException {
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

    /**
     * Returns Stats about the DailyChuck.
     *
     * @return
     */
    public @RequestMapping(value = "/feed/daily-chuck/stats", method = RequestMethod.GET, headers = HttpHeaders.ACCEPT + "="
            + MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) MailingListStatistic dailyChuckStats() {
        MailingListStatistic mailingListStatistic = mailchimpService.fetchListStats(dailyChuckListId);

        return mailingListStatistic;
    }

    /**
     * Returns the current DailyChuck in RSS format.
     *
     * @return dailyChuck
     * @throws IOException Thrown if {@link DailyChuck} can't ber persisted.
     */
    public @RequestMapping(value = { "/feed/daily-chuck.xml", "/feed/daily-chuck" }, method = RequestMethod.GET, headers = HttpHeaders.ACCEPT + "="
            + MediaType.TEXT_XML_VALUE, produces = MediaType.APPLICATION_RSS_XML_VALUE) View dailyChuckRss() throws IOException {
        DailyChuck dailyChuck = dailyChuckService.getDailyChuck();

        MailingListStatistic mailingListStatistic = mailchimpService.fetchListStats(dailyChuckListId);

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
