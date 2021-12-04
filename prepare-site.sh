#! /bin/bash

set -eu
cd $(dirname $0)

# exapnd benchmark data to the working tree
DESTINATION=benchmark-data
rm -r "$DESTINATION"
git read-tree --prefix="$DESTINATION" -u remotes/origin/benchmark-data
git reset HEAD "$DESTINATION"

# build dokka
cd kohomology
./gradlew dokkaHtml
./gradlew dokkacovWriteJson
