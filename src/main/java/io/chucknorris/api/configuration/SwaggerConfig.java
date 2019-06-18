package io.chucknorris.api.configuration;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicate;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  /**
   * Returns api docket bean {@link Docket}.
   */
  public @Bean Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("io.chucknorris"))
        .paths(paths())
        .build()
        .apiInfo(getApiInfo());
  }

  private ApiInfo getApiInfo() {
    return new ApiInfo(
        "Chuck Norris IO",
        "chucknorris.io is a free JSON API for hand curated Chuck Norris facts.\n"
            + "\n"
            + "Chuck Norris facts are satirical factoids about martial artist and actor Chuck "
            + "Norris that have become an Internet phenomenon and as a result have become "
            + "widespread in popular culture. The 'facts' are normally absurd hyperbolic claims "
            + "about Norris' toughness, attitude, virility, sophistication, and masculinity.\n"
            + "\n"
            + "Chuck Norris facts have spread around the world, leading not only to translated "
            + "versions, but also spawning localized versions mentioning country-specific "
            + "advertisements and other Internet phenomena. Allusions are also sometimes made to "
            + "his use of roundhouse kicks to perform seemingly any task, his large amount of body "
            + "hair with specific regard to his beard, and his role in the action television "
            + "series Walker, Texas Ranger.",
        "2.0.0",
        "https://api.chucknorris.io/",
        new Contact("Mathias Schilling", "https://www.matchilling.com", "m@matchilling.com"),
        "GNU General Public License v3.0",
        "https://github.com/chucknorris-io/chuck-api/blob/master/LICENSE",
        Collections.emptyList()
    );
  }

  private Predicate<String> paths() {
    return or(
        regex("/jokes.*"),
        regex("/feed.*")
    );
  }
}
