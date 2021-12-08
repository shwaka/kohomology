#! /bin/bash

set -eu
cd $(dirname $0)
ROOT_DIR=$(pwd)

# exapnd benchmark data to the working tree
cd "$ROOT_DIR"
DESTINATION=benchmark-data
echo "Expand benchmark data to '$DESTINATION/'"
rm -rf "$DESTINATION"
git read-tree --prefix="$DESTINATION" -u remotes/origin/benchmark-data
git reset HEAD "$DESTINATION"

# build dokka
cd "$ROOT_DIR"/kohomology
echo "Generate dokka document"
./gradlew dokkaHtml
echo "Generate dokka coverage"
./gradlew dokkacovWriteJson

# build kohomology-js
cd "$ROOT_DIR"/website/kohomology-js
echo build kohomology-js
./gradlew build
