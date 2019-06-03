package io.chucknorris.api.lib.dailychuck;

import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DailyChuckTest {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private DailyChuck dailyChuck;
    private DailyChuckIssue dailyChuckIssue;

    @Before
    public void setUp() throws ParseException {
        dailyChuckIssue = new DailyChuckIssue();
        dailyChuckIssue.setDate(
            dateFormat.parse("2019-01-01")
        );
        dailyChuckIssue.setJokeId("c5k7tulvqjs76evwb3brfg");

        dailyChuck = new DailyChuck();
        dailyChuck.setIssues(new DailyChuckIssue[]{dailyChuckIssue});
    }

    @Test
    public void testFindIssueByJokeIdReturnsNullDoesNotExist() {
        assertEquals(dailyChuck.findIssueByJokeId("does-not-exist"), null);
    }

    @Test
    public void testFindIssueByJokeIdReturnsDailyChuckIssueIfDoesExist() {
        assertEquals(dailyChuck.findIssueByJokeId("c5k7tulvqjs76evwb3brfg"), dailyChuckIssue);
    }

    @Test
    public void testFindIssueByDateReturnsNullDoesNotExist() throws ParseException {
        assertEquals(dailyChuck.findIssueByDate(
            dateFormat.parse("2019-01-02")
        ), null);
    }

    @Test
    public void testFindIssueByDateDailyChuckIssueIfDoesExist() throws ParseException {
        assertEquals(dailyChuck.findIssueByDate(
            dateFormat.parse("2019-01-01")
        ), dailyChuckIssue);
    }
}