#! /bin/bash

(
    cd ../kohomology
    ./gradlew publishAllPublicationsToBenchmarkRepository
)

./gradlew benchmark
