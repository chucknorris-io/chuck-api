package io.chucknorris.api.feed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.chucknorris.api.feed.dailychuck.DailyChuck;
import io.chucknorris.api.feed.dailychuck.DailyChuckIssue;
import io.chucknorris.api.feed.dailychuck.DailyChuckRss;
import io.chucknorris.api.feed.dailychuck.DailyChuckService;
import io.chucknorris.api.joke.JokeRepository;
import io.chucknorris.lib.DateUtil;
import io.chucknorris.lib.event.EventService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FeedControllerTest {

    private DailyChuck dailyChuck;
    private DailyChuckIssue dailyChuckIssue;

    @Mock
    private DailyChuckService dailyChuckService;

    @Mock
    private DateUtil dateUtil;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Mock
    private EventService eventService;

    @InjectMocks
    private FeedController feedController;

    @Mock
    private JokeRepository jokeRepository;

    @Before
    public void setUp() throws ParseException {
        dailyChuckIssue = new DailyChuckIssue();
        dailyChuckIssue.setDate(
            dateFormat.parse("2019-01-01")
        );
        dailyChuckIssue.setJokeId("c5k7tulvqjs76evwb3brfg");

        dailyChuck = new DailyChuck();
        dailyChuck.setIssues(new DailyChuckIssue[]{dailyChuckIssue});
        dailyChuck.setIssueNumber(Long.valueOf(1));
    }

    @Test
    public void testDailyChuckJsonReturnsDailyChuckWithoutComposingANewIssueIfItHasAlreadyBeenIssued()
        throws IOException, ParseException
    {
        when(dailyChuckService.getDailyChuck()).thenReturn(dailyChuck);
        when(dateUtil.now()).thenReturn(dateFormat.parse("2019-01-01"));

        assertEquals(
            dailyChuck,
            feedController.dailyChuckJson()
        );

        verify(dailyChuckService, times(1)).getDailyChuck();
        verifyNoMoreInteractions(dailyChuckService);

        verify(dateUtil, times(1)).now();
        verifyNoMoreInteractions(dateUtil);

        verify(eventService, times(0)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testDailyChuckJsonReturnsDailyChuckWithComposingANewIssue()
        throws IOException, ParseException
    {
        DailyChuckIssue newDailyChuckIssue = new DailyChuckIssue();

        when(dailyChuckService.getDailyChuck()).thenReturn(dailyChuck);
        when(dateUtil.now()).thenReturn(dateFormat.parse("2019-01-02"));
        when(dailyChuckService.composeDailyChuckIssue(any())).thenReturn(newDailyChuckIssue);

        assertEquals(
            dailyChuck,
            feedController.dailyChuckJson()
        );

        verify(dailyChuckService, times(1)).getDailyChuck();
        verify(dailyChuckService, times(1)).composeDailyChuckIssue(any());
        verify(dailyChuckService, times(1)).persist(dailyChuck);
        verifyNoMoreInteractions(dailyChuckService);

        verify(dateUtil, times(1)).now();
        verifyNoMoreInteractions(dateUtil);

        verify(eventService, times(1)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testDailyChuckRssReturnsDailyChuckWithoutComposingANewIssueIfItHasAlreadyBeenIssued()
        throws IOException, ParseException
    {
        DailyChuckRss dailyChuckRss = new DailyChuckRss("", dailyChuck, jokeRepository);

        when(dailyChuckService.getDailyChuck()).thenReturn(dailyChuck);
        when(dateUtil.now()).thenReturn(dateFormat.parse("2019-01-01"));
        when(dailyChuckService.toRss(dailyChuck)).thenReturn(dailyChuckRss);

        assertEquals(
            dailyChuckRss,
            feedController.dailyChuckRss()
        );

        verify(dailyChuckService, times(1)).getDailyChuck();
        verify(dailyChuckService, times(1)).toRss(dailyChuck);

        verify(dateUtil, times(1)).now();
        verifyNoMoreInteractions(dateUtil);

        verify(eventService, times(0)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testDailyChuckRssReturnsDailyChuckWithComposingANewIssue()
        throws IOException, ParseException
    {
        DailyChuckRss dailyChuckRss = new DailyChuckRss("", dailyChuck, jokeRepository);
        DailyChuckIssue newDailyChuckIssue = new DailyChuckIssue();

        when(dailyChuckService.getDailyChuck()).thenReturn(dailyChuck);
        when(dateUtil.now()).thenReturn(dateFormat.parse("2019-01-02"));
        when(dailyChuckService.composeDailyChuckIssue(any())).thenReturn(newDailyChuckIssue);
        when(dailyChuckService.toRss(dailyChuck)).thenReturn(dailyChuckRss);

        assertEquals(
            dailyChuckRss,
            feedController.dailyChuckRss()
        );

        verify(dailyChuckService, times(1)).getDailyChuck();
        verify(dailyChuckService, times(1)).composeDailyChuckIssue(any());
        verify(dailyChuckService, times(1)).persist(dailyChuck);
        verify(dailyChuckService, times(1)).toRss(dailyChuck);
        verifyNoMoreInteractions(dailyChuckService);

        verify(dateUtil, times(1)).now();
        verifyNoMoreInteractions(dateUtil);

        verify(eventService, times(1)).publishEvent(any());
        verifyNoMoreInteractions(eventService);
    }
}