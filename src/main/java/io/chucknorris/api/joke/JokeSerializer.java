package io.chucknorris.api.joke;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;

public class JokeSerializer extends StdSerializer<Joke> {

  @Value("${application.base_url}")
  private String baseUrl;

  public JokeSerializer() {
    this(null);
  }

  public JokeSerializer(Class<Joke> t) {
    super(t);
  }

  @Override
  public void serialize(Joke joke, JsonGenerator jsonGenerator, SerializerProvider provider)
      throws IOException {
    jsonGenerator.writeStartObject();

    String[] categories = joke.getCategories() != null && joke.getCategories().length > 0
        ? joke.getCategories()
        : new String[0];
    jsonGenerator.writeArrayFieldStart("categories");
    for (String category : categories) {
      jsonGenerator.writeString(category);
    }
    jsonGenerator.writeEndArray();

    jsonGenerator.writeStringField("created_at", joke.getCreatedAt());
    jsonGenerator.writeStringField("icon_url", joke.getIconUrl());
    jsonGenerator.writeStringField("id", joke.getId());
    jsonGenerator.writeStringField("updated_at", joke.getUpdatedAt());
    jsonGenerator.writeStringField("url", "https://" + baseUrl + "/jokes/" + joke.getId());
    jsonGenerator.writeStringField("value", joke.getValue());
    jsonGenerator.writeEndObject();
  }
}