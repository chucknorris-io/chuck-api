package io.chucknorris.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;

@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Entity
@Table(name = "joke")
public class Joke implements Serializable {

  @Type(type = "jsonb")
  @Column(name = "categories", columnDefinition = "jsonb")
  private String[] categories;

  @Column(name = "created_at")
  @JsonProperty("created_at")
  private String createdAt;

  @Transient
  @JsonProperty("icon_url")
  private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

  @Id
  @Column(name = "joke_id", updatable = false, nullable = false)
  @JsonProperty("id")
  private String id;

  @Column(name = "updated_at")
  @JsonProperty("updated_at")
  private String updatedAt;

  @Transient
  @JsonProperty("url")
  private String url;

  @Column(name = "value")
  @JsonProperty("value")
  private String value;

  public String[] getCategories() {
    return categories;
  }

  public Joke setCategories(String[] categories) {
    this.categories = categories;

    return this;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public Joke setCreatedAt(String createdAt) {
    this.createdAt = createdAt;

    return this;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public Joke setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;

    return this;
  }

  public String getId() {
    return id;
  }

  public Joke setId(String id) {
    this.id = id;

    return this;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public Joke setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;

    return this;
  }

  public String getUrl() {
      return "/jokes/" + id;
  }

  public String getValue() {
    return value;
  }

  public Joke setValue(String value) {
    this.value = value;

    return this;
  }
}
