package io.chucknorris.api.lib.slack.Impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SlackServiceTest {

    @InjectMocks
    private SlackService slackService = new SlackService();

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
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