# CHUCKNORRIS.IO [![CircleCI](https://circleci.com/gh/chucknorris-io/chuck-api.svg?style=svg)](https://circleci.com/gh/chucknorris-io/chuck-api)

[chucknorris.io](https://api.chucknorris.io) is a free JSON API for hand curated Chuck Norris facts.

Chuck Norris facts are satirical factoids about martial artist and actor Chuck Norris that have become an Internet
phenomenon and as a result have become widespread in popular culture. The 'facts' are normally absurd hyperbolic claims
about Norris' toughness, attitude, virility, sophistication, and masculinity.

Chuck Norris facts have spread around the world, leading not only to translated versions, but also spawning localized
versions mentioning country-specific advertisements and other Internet phenomena. Allusions are also sometimes made to
his use of roundhouse kicks to perform seemingly any task, his large amount of body hair with specific regard to his
beard, and his role in the action television series Walker, Texas Ranger.

## Usage

```shell
# Retrieve a random chuck joke
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/random' \
       --header 'accept: application/json'

# Retrieve a random chuck joke by one or more categories
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/random?category=dev,explicit' \
       --header 'accept: application/json'

# Retrieve a random personalized chuck joke
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/random?name=Bob' \
       --header 'accept: application/json'

# Retrieve a random personalized chuck joke by one or more categories
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/random?name=Bob&category=dev,explicit' \
       --header 'accept: application/json'

# Retrieve a list of available categories
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/categories' \
       --header 'accept: application/json'

# Free text search
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/search?query={query}' \
       --header 'accept: application/json'
```

Example response:

```json
{
  "categories": ["dev"],
  "created_at": "2019-06-02 08:47:39.43184",
  "icon_url": "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
  "id": "bwoz2uwsqnwmyawyxdvo1w",
  "updated_at": "2019-06-02 08:47:39.43184",
  "url": "https://api.chucknorris.io/jokes/bwoz2uwsqnwmyawyxdvo1w",
  "value": "Chuck Norris finished World of Warcraft."
}
```

For more examples check the [Swagger documentation](https://api.chucknorris.io/documentation) and have a look into the [Postman collection](./postman/io.chucknorris.api.postman_collection.json).

## Local development

An example of all required application variables can be found in the [application-example.properties](./src/main/resources/application-example.properties).

```sh
# Start all application dependencies
$ docker-compose up     # Will run as a long running process
$ docker-compose up -d  # Will run in background

# Start the application itself
$ SPRING_PROFILES_ACTIVE=development ./gradlew clean bootRun
```

**Urls:**

- Application: [http://localhost:8080](http://localhost:8080)
- Grafana: [http://localhost:3000](http://localhost:3000)
- PostgreSQL: [http://localhost:5432](http://localhost:5432)
- Prometheus: [http://localhost:9090](http://localhost:9090)
- Swagger UI: [http://localhost:4567](http://localhost:4567)

## License

This distribution is covered by the **GNU GENERAL PUBLIC LICENSE**, Version 3, 29 June 2007.

## Support & Contact

Having trouble with this repository? Check out the documentation at the repository's site or contact m@matchilling.com and we’ll help you sort it out.

Happy Coding

:v:
