#! /bin/bash

set -eu
# cd $(dirname $0)
cd $(git rev-parse --show-toplevel) # go to the root of the repository
ROOT_DIR=$(pwd)

# exapnd benchmark data to the working tree
cd "$ROOT_DIR"
for NAME in benchmark-data benchmark-data-website; do
    DESTINATION=$NAME
    BRANCH=remotes/origin/$NAME
    echo "--- Expand benchmark data to '$DESTINATION/'---"
    rm -rf "$DESTINATION"
    git read-tree --prefix="$DESTINATION" -u $BRANCH
    git reset HEAD "$DESTINATION"
    sed "s/window.BENCHMARK_DATA = //" < "$DESTINATION/dev/bench/data.js" > "$DESTINATION/dev/bench/benchmarkData.json"
done

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
