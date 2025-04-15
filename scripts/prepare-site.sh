#! /bin/bash

set -eux
# cd $(dirname $0)
cd $(git rev-parse --show-toplevel) # go to the root of the repository
ROOT_DIR=$(pwd)

command=${1-all}

function prepare_benchmark_data() {
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
}

function prepare_dokka() {
    # build dokka
    cd "$ROOT_DIR"/kohomology
    echo "--- Generate dokka document ---"
    ./gradlew dokkaHtml
    echo "--- Generate dokka coverage ---"
    ./gradlew dokkacovWriteJson
}

function prepare_kohomology_js() {
    # build kohomology-js
    cd "$ROOT_DIR"/website/kohomology-js
    echo "--- build kohomology-js ---"
    # ./gradlew build # `./gradlew build` contains `./gradlew jsTest`
    ./gradlew assemble
}

case "$command" in
    all)
        prepare_benchmark_data
        prepare_dokka
        prepare_kohomology_js;;
    benchmark-data) prepare_benchmark_data;;
    dokka) prepare_dokka;;
    kohomology-js) prepare_kohomology_js;;
    *) echo "Invalid command: $command"
esac
