package io.chucknorris.api.slack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

@RunWith(MockitoJUnitRunner.class)
public class SlackServiceTest {

    @InjectMocks
    private SlackService slackService = new SlackService();

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(slackService, "whitelistedCategories", "career,celebrity,dev,fashion,food,money,movie,travel");
        ReflectionTestUtils.setField(slackService, "clientId", "slack.oauth.client_id");
        ReflectionTestUtils.setField(slackService, "clientSecret", "slack.oauth.client_secret");
        ReflectionTestUtils.setField(slackService, "redirectUrl", "slack.oauth.redirect_uri");
    }

    @Test
    public void testComposeAuthorizeUri() {
        UriComponents authorizeUri = slackService.composeAuthorizeUri();

        assertEquals(
            "https://slack.com/oauth/authorize/?client_id=slack.oauth.client_id&redirect_uri=slack.oauth.redirect_uri&scope=bot%20commands",
            authorizeUri.toUriString()
        );
    }

    @Test
    public void testFilterNonWhitelistedCategories() {
        assertArrayEquals(
            slackService.filterNonWhitelistedCategories(
                new String[]{
                    "career",
                    "celebrity",
                    "dev",
                    "explicit",
                    "fashion",
                    "food",
                    "money",
                    "movie",
                    "travel"
                }
            ),
            new String[]{
                "career",
                "celebrity",
                "dev",
                "fashion",
                "food",
                "money",
                "movie",
                "travel"
            }
        );
    }

    @Test
    public void testGetWhitelistedCategoriesReturnsArrayOfCategories() {
        assertArrayEquals(
            slackService.getWhitelistedCategories(),
            new String[]{
                "career",
                "celebrity",
                "dev",
                "fashion",
                "food",
                "money",
                "movie",
                "travel"
            }
        );
    }

    @Test
    public void testIfGivenCategoryIsWhitelisted() {
        assertFalse(slackService.isWhitelistedCategory("explicit"));
        assertFalse(slackService.isWhitelistedCategory("religion"));
        assertTrue(slackService.isWhitelistedCategory("dev"));
    }

    @Test
    public void testRequestAccessTokenSendsRequestAndRetunsToken() {
        String code = "my-super-secret-code";
        AccessToken accessToken = new AccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "slack.oauth.client_id");
        map.add("client_secret", "slack.oauth.client_secret");
        map.add("code", code);
        map.add("redirect_uri", "slack.oauth.redirect_uri");

        when(restTemplate.exchange(
            "https://slack.com/api/oauth.access",
            HttpMethod.POST,
            new HttpEntity<MultiValueMap<String, String>>(map, headers),
            AccessToken.class
        )).thenReturn(
            new ResponseEntity(accessToken, HttpStatus.OK)
        );

        AccessToken response = slackService.requestAccessToken(code);
        assertEquals(accessToken, response);

        verify(restTemplate, times(1)).exchange(
            "https://slack.com/api/oauth.access",
            HttpMethod.POST,
            new HttpEntity<MultiValueMap<String, String>>(map, headers),
            AccessToken.class
        );
        verifyNoMoreInteractions(restTemplate);
    }
}