package io.chucknorris.api.joke;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class JokeSearchResult implements Serializable {

  @JsonProperty("total")
  private int total;

  @JsonProperty("result")
  private Joke[] result;

  public JokeSearchResult(Joke[] jokes) {
    this.total = jokes.length;
    this.result = jokes;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public Joke[] getResult() {
    return result;
  }

  public void setResult(Joke[] result) {
    this.result = result;
  }
}
