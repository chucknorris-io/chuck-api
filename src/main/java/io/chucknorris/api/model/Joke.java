package io.chucknorris.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.chucknorris.api.lib.serializer.JokeSerializer;
import io.swagger.annotations.ApiModelProperty;
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
@JsonSerialize(using = JokeSerializer.class)
public class Joke implements Serializable {

  @ApiModelProperty(
      name = "categories",
      notes = "List of categories."
  )
  @Column(name = "categories", columnDefinition = "jsonb")
  @Type(type = "jsonb")
  private String[] categories;

  @ApiModelProperty(
      example = "2019-06-02 08:47:39.408742",
      name = "created_at",
      notes = "Timestamp when the joke was created."
  )
  @Column(name = "created_at")
  private String createdAt;

  @ApiModelProperty(
      accessMode = ApiModelProperty.AccessMode.READ_ONLY,
      example = "nzf46249t8cf7wgz3rf_rg",
      name = "icon_url",
      notes = "Absolute URL of the Chuck Norris icon."
  )
  @Transient
  private String iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png";

  @ApiModelProperty(
// ^[a-zA-Z0-9_-]{22}$
      example = "nzf46249t8cf7wgz3rf_rg",
      name = "joke_id",
      notes = "URL-safe Base64-encoded UUID for a joke."
  )
  @Id
  @Column(name = "joke_id", updatable = false, nullable = false)
  private String id;

  @ApiModelProperty(
      example = "2019-06-02 08:47:39.408742",
      name = "updated_at",
      notes = "Timestamp when the joke was updated."
  )
  @Column(name = "updated_at")
  private String updatedAt;

  @ApiModelProperty(
      accessMode = ApiModelProperty.AccessMode.READ_ONLY,
      example = "https://api.chucknorris.io/jokes/nzf46249t8cf7wgz3rf_rg",
      name = "url",
      notes = "Absolute URL of the joke."
  )
  @Transient
  private String url;

  @ApiModelProperty(
      example = "Chuck Norris doesn't have disk latency because the hard drive knows to hurry the hell up.",
      name = "value",
      notes = "The contents of an incredible funny joke.",
      required = true
  )
  @Column(name = "value")
  private String value;

  public String[] getCategories() {
    return categories;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public String getId() {
    return id;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public String getUrl() {
    return url;
  }

  public String getValue() {
    return value;
  }

  public Joke setCategories(String[] categories) {
    this.categories = categories;
    return this;
  }

  public Joke setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public Joke setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

  public Joke setId(String id) {
    this.id = id;
    return this;
  }

  public Joke setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Joke setUrl(String url) {
    this.url = url;
    return this;
  }

  public Joke setValue(String value) {
    this.value = value;
    return this;
  }
}
