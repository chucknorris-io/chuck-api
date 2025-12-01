#!/bin/sh

readonly SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
readonly MODULE_DIR=$(cd "${SCRIPT_DIR}/.." && pwd)
readonly PROJECT_DIR=$(cd "${SCRIPT_DIR}/../.." && pwd)

./gradlew --build-file "$PROJECT_DIR/chucknorris-web/build.gradle" clean docker

docker compose --file "$MODULE_DIR/docker-compose.yml" up \
   --exit-code-from "integration-test" \
   --quiet-pull \
   --remove-orphans