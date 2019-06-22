package io.chucknorris.api.slack;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SlackService {

  @Value("${slack.oauth.client_id}")
  private String clientId;

  @Value("${slack.oauth.client_secret}")
  private String clientSecret;

  @Value("${slack.oauth.redirect_uri}")
  private String redirectUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${slack.content.whitelisted_categories}")
  private String whitelistedCategories;

  /**
   * Composes the authorize uri.
   */
  public UriComponents composeAuthorizeUri() {
    //@see  https://api.slack.com/docs/oauth-scopes
    MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<>();
    urlQueryParams.set("client_id", clientId);
    urlQueryParams.set("redirect_uri", redirectUrl);
    urlQueryParams.set("scope", "bot commands");

    return UriComponentsBuilder
        .newInstance()
        .scheme("https")
        .host("slack.com")
        .path("/oauth/authorize/")
        .queryParams(urlQueryParams)
        .build()
        .encode();
  }

  /**
   * Filters all non-whitelisted categories from array.
   */
  public String[] filterNonWhitelistedCategories(String[] categories) {
    return Arrays.stream(categories).filter(
        category -> isWhitelistedCategory(category)
    ).toArray(String[]::new);
  }

  /**
   * Returns an array of whitelisted categories.
   */
  public String[] getWhitelistedCategories() {
    return whitelistedCategories != null
        ? whitelistedCategories.split(",")
        : new String[]{};
  }

  /**
   * Checks if a given category is whitelisted.
   */
  public Boolean isWhitelistedCategory(String category) {
    return Arrays.stream(
        getWhitelistedCategories()
    ).anyMatch(category::equals);
  }

  /**
   * Requests an access token from Slack.
   */
  public AccessToken requestAccessToken(final String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    map.add("code", code);
    map.add("redirect_uri", redirectUrl);

    ResponseEntity<AccessToken> responseEntity = restTemplate.exchange(
        "https://slack.com/api/oauth.access",
        HttpMethod.POST,
        new HttpEntity<MultiValueMap<String, String>>(map, headers),
        AccessToken.class
    );

    return responseEntity.getBody();
  }
}
