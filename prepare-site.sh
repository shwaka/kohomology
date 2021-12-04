#! /bin/bash

set -eu
cd $(dirname $0)

# exapnd benchmark data to the working tree
DESTINATION=benchmark-data
echo "Expand benchmark data to '$DESTINATION/'"
rm -rf "$DESTINATION"
git read-tree --prefix="$DESTINATION" -u remotes/origin/benchmark-data
git reset HEAD "$DESTINATION"

# build dokka
cd kohomology
echo "Generate dokka document"
./gradlew dokkaHtml
echo "Generate dokka coverage"
./gradlew dokkacovWriteJson
