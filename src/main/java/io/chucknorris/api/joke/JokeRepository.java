package io.chucknorris.api.joke;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@TypeDefs({
    @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public interface JokeRepository extends JpaRepository<Joke, String> {

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM joke AS j "
          + "WHERE lower(j.value) LIKE CONCAT('%', lower(:query), '%')",
      nativeQuery = true
  )
  Page<Joke> findByValueContains(@Param("query") final String query, Pageable pageable);

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM find_by_by_value_contains_and_filter(:query, :categories) AS j",
      countQuery = "SELECT count(*) "
          + "FROM find_by_by_value_contains_and_filter(:query, :categories)",
      nativeQuery = true
  )
  Page<Joke> findByValueContainsAndFilter(
      @Param("query") final String query,
      @Param("categories") final String categories,
      Pageable pageable
  );

  @Query(
      value = "SELECT j.categories->>0 FROM joke j WHERE j.categories IS NOT NULL "
          + "GROUP BY j.categories->>0 "
          + "ORDER BY j.categories->>0 ASC",
      nativeQuery = true
  )
  String[] findAllCategories();

  @Query(
      value = "WITH joke AS( SELECT joke_id, ROW_NUMBER() "
          + "OVER (ORDER BY joke.created_at ASC, joke.joke_id ASC) AS row_number FROM joke), "
          + "current AS ( SELECT * FROM joke WHERE joke_id = :id LIMIT 1 ), "
          + "prev AS ( "
          + "SELECT (CASE WHEN (SELECT min(row_number) FROM joke) >= current.row_number - 1 "
          + "THEN (SELECT joke_id FROM joke WHERE row_number = (SELECT max(row_number) FROM joke)) "
          + "ELSE (SELECT joke_id FROM joke WHERE row_number = current.row_number - 1) END) "
          + "AS joke_id FROM joke, current LIMIT 1 ), "
          + "next AS ( "
          + "SELECT (CASE WHEN (SELECT max(row_number) FROM joke) <= current.row_number + 1 "
          + "THEN (SELECT joke_id FROM joke WHERE row_number = (SELECT min(row_number) FROM joke)) "
          + "ELSE (SELECT joke_id FROM joke WHERE row_number = current.row_number + 1) END) "
          + "AS joke_id FROM joke, current LIMIT 1 ) "
          + "SELECT "
          + "current.joke_id AS current_joke_id, "
          + "next.joke_id AS next_joke_id, "
          + "prev.joke_id AS prev_joke_id "
          + "FROM current, prev, next;",
      nativeQuery = true
  )
  String getJokeWindow(@Param("id") final String id);

  @Query(
      value = "SELECT "
          + "j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM joke AS j "
          + "ORDER BY RANDOM() LIMIT 1;",
      nativeQuery = true
  )
  Joke getRandomJoke();

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM get_random_personalized_joke(:substitute, null) AS j;",
      nativeQuery = true
  )
  Joke getRandomPersonalizedJoke(
      @Param("substitute") final String substitute
  );

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM get_random_personalized_joke(:substitute, :categories) AS j;",
      nativeQuery = true
  )
  Joke getRandomPersonalizedJokeByCategories(
      @Param("substitute") final String substitute,
      @Param("categories") final String categories
  );

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM joke AS j "
          + "WHERE j.categories IS NOT NULL AND j.categories->>0 = :category "
          + "ORDER BY RANDOM() LIMIT 1;",
      nativeQuery = true
  )
  Joke getRandomJokeByCategory(@Param("category") final String category);

  /**
   * Gets a random joke by a comma separated list of categories. Matches if any of the joke's
   * categories equals one of the provided categories.
   */
  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM get_joke_random_by_categories(:categories) AS j;",
      nativeQuery = true
  )
  Joke getRandomJokeByCategories(@Param("categories") final String categories);

  @Query(
      value = "SELECT j.categories, j.created_at, j.joke_id, j.updated_at, j.value "
          + "FROM joke AS j "
          + "WHERE lower(j.value) LIKE CONCAT('%', lower(:query), '%');",
      nativeQuery = true
  )
  Joke[] searchByQuery(@Param("query") final String query);
}