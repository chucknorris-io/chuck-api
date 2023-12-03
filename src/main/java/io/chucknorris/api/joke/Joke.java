package io.chucknorris.api.joke;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
@JsonSerialize(using = JokeSerializer.class)
@NoArgsConstructor
@Table(name = "joke")
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Joke implements Serializable {

  @ApiModelProperty(
      name = "categories",
      notes = "MailingList of categories."
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
  final private String iconUrl = "https://api.chucknorris.io/img/avatar/chuck-norris.png";

  @ApiModelProperty(
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
      example = "Chuck Norris doesn't have disk latency because "
          + "the hard drive knows to hurry the hell up.",
      name = "value",
      notes = "The contents of an incredible funny joke.",
      required = true
  )
  @Column(name = "value")
  private String value;
}
