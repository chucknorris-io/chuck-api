package io.chucknorris.api.privacy;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PrivacyController {

  /**
   * Returns privacy view.
   */
  public @RequestMapping(
      value = "/privacy",
      method = RequestMethod.GET,
      headers = HttpHeaders.ACCEPT + "=" + MediaType.TEXT_HTML_VALUE,
      produces = MediaType.TEXT_HTML_VALUE
  ) String get() {
    return "privacy";
  }
}
