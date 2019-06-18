package io.chucknorris.api.configuration;

import io.chucknorris.lib.mailchimp.MailchimpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailchimpConfig {

  @Value("${mailchimp.api_key}")
  private String apiKey;

  /**
   * Returns a new {@link MailchimpService} instance.
   */
  public @Bean MailchimpService mailchimpService() {
    String dataCenter = apiKey.substring(
        apiKey.length() - 4
    );
    String baseUrl = "https://" + dataCenter + ".api.mailchimp.com";

    return new MailchimpService(apiKey, baseUrl);
  }
}
