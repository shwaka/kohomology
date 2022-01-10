#! /bin/bash

set -eu

cd $(git rev-parse --show-toplevel) # go to the root of the repository

cd sample
for f in $(ls src/main/kotlin); do
    ./gradlew run -DsampleName=$f
done
