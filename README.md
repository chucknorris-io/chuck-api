[![Donate to this project using patreon.com](https://img.shields.io/badge/patreon-donate-yellow.svg)](https://www.patreon.com/matchilling) [![Build Status](https://travis-ci.org/chucknorris-io/chuck-api.svg?branch=master)](https://travis-ci.org/chucknorris-io/chuck-api)

# CHUCKNORRIS.IO

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
       --header 'accept: (application/json|text/plain)'

# Add an optional `category` parameter to get a random joke from the given category
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/random?category=dev' \
       --header 'accept: (application/json|text/plain)'

# Retrieve a list of available categories
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/categories' \
       --header 'accept: (application/json|text/plain)'

# Free text search
$ curl --request GET \
       --url 'https://api.chucknorris.io/jokes/search?query={query}' \
       --header 'accept: (application/json|text/plain)'
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

To start the stack using docker you need to set a couple of environment variables which are defined in an env file in the root directory of the project. All required variable identifiers are shipped in the [application-example.properties](./src/main/resources/application-example.properties) file which you can use as an example.

```sh
$ docker-compose up     # Will run as a long running process
$ docker-compose up -d  # Will run in background
```

**Urls:**

- API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:4567](http://localhost:4567)

## License

This distribution is covered by the **GNU GENERAL PUBLIC LICENSE**, Version 3, 29 June 2007.

## Support & Contact

Having trouble with this repository? Check out the documentation at the repository's site or contact m@matchilling.com and we’ll help you sort it out.

Happy Coding

:v:
