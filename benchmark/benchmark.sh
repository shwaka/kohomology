#! /bin/bash

set -eu

(
    cd ../kohomology
    ./gradlew publishAllPublicationsToBenchmarkRepository
)

./gradlew benchmark
