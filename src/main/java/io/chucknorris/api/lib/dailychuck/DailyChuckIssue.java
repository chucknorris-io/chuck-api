package io.chucknorris.api.lib.dailychuck;

import java.util.Date;

public class DailyChuckIssue {
    private Date date;
    private String jokeId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getJokeId() {
        return jokeId;
    }

    public void setJokeId(String jokeId) {
        this.jokeId = jokeId;
    }
}
