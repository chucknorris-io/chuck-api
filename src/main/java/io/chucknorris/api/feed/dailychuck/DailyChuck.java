package io.chucknorris.api.feed.dailychuck;

import java.util.Calendar;
import java.util.Date;

public class DailyChuck {

  private Long issueNumber;
  private DailyChuckIssue[] issues;

  public Long getIssueNumber() {
    return issueNumber;
  }

  public void setIssueNumber(Long issueNumber) {
    this.issueNumber = issueNumber;
  }

  public DailyChuckIssue[] getIssues() {
    return issues;
  }

  public void setIssues(DailyChuckIssue[] issues) {
    this.issues = issues;
  }

  /**
   * Adds a DailyChuckIssue {@link DailyChuckIssue} to the DailyChuck instance and increases the
   * issue number {@link DailyChuck#issueNumber} by one.
   */
  public void addIssue(DailyChuckIssue dailyChuckIssue) {
    DailyChuckIssue[] dailyChuckIssues = new DailyChuckIssue[issues.length + 1];

    for (int i = 0; i < issues.length; i++) {
      dailyChuckIssues[i] = issues[i];
    }

    dailyChuckIssues[issues.length] = dailyChuckIssue;

    issues = dailyChuckIssues;
    issueNumber += 1;
  }

  /**
   * Finds an issue {@link DailyChuckIssue} by joke id.
   */
  public DailyChuckIssue findIssueByJokeId(String jokeId) {
    for (DailyChuckIssue issue : issues) {
      if (issue.getJokeId().equals(jokeId)) {
        return issue;
      }
    }
    return null;
  }

  /**
   * Finds an issue {@link DailyChuckIssue} by a given date.
   */
  public DailyChuckIssue findIssueByDate(Date date) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);

    for (DailyChuckIssue issue : issues) {
      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(issue.getDate());

      boolean isSameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
          && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

      if (isSameDay) {
        return issue;
      }
    }
    return null;
  }
}
