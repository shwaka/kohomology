#! /bin/bash

set -eu

(
    cd ../kohomology
    ./gradlew publishAllPublicationsToTemporaryRepository
)

./gradlew benchmark
