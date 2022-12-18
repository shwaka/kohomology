#! /bin/bash

set -eu
# cd $(dirname $0)
cd $(git rev-parse --show-toplevel) # go to the root of the repository
ROOT_DIR=$(pwd)

# exapnd benchmark data to the working tree
cd "$ROOT_DIR"
DESTINATION=benchmark-data
echo "--- Expand benchmark data to '$DESTINATION/'---"
rm -rf "$DESTINATION"
git read-tree --prefix="$DESTINATION" -u remotes/origin/benchmark-data
git reset HEAD "$DESTINATION"
sed "s/window.BENCHMARK_DATA = //" < "$DESTINATION/dev/bench/data.js" > "$DESTINATION/dev/bench/benchmarkData.json"

# build dokka
cd "$ROOT_DIR"/kohomology
echo "--- Generate dokka document ---"
./gradlew dokkaHtml
echo "--- Generate dokka coverage ---"
./gradlew dokkacovWriteJson

# build kohomology-js
cd "$ROOT_DIR"/website/kohomology-js
echo "--- build kohomology-js ---"
./gradlew build
