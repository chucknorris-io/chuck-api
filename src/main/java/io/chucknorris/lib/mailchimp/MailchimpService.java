package io.chucknorris.lib.mailchimp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MailchimpService {

  private String apiKey;
  private String baseUrl;

  @Autowired
  private RestTemplate restTemplate;

  public MailchimpService(String apiKey, String baseUrl) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  /**
   * Fetch mailing list stats by a given list id.
   *
   * @param listId The list id
   * @return list
   */
  public MailingListStatistic fetchListStats(final String listId) {
    return fetchList(listId).getMailingListStatistic();
  }

  private MailingList fetchList(final String listId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    headers.add(HttpHeaders.AUTHORIZATION, "apikey " + this.apiKey);

    ResponseEntity<MailingList> responseEntity = restTemplate.exchange(
        baseUrl + "/3.0/lists/" + listId,
        HttpMethod.GET,
        new HttpEntity<>(null, headers),
        MailingList.class
    );

    return responseEntity.getBody();
  }
}
