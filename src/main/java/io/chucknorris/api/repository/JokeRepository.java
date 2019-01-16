package io.chucknorris.api.repository;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import io.chucknorris.api.model.Joke;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@TypeDefs({
    @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public interface JokeRepository extends JpaRepository<Joke, String> {
  @Query(
      value = "SELECT j.categories->>0 FROM joke j WHERE j.categories IS NOT NULL GROUP BY j.categories->>0 ORDER BY j.categories->>0 ASC",
      nativeQuery = true
  )
  String[] findAllCategories();
}