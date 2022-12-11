#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
target=${2-}
degree=${3-}

if [ "$command" = version ]; then
    while read line; do
        echo "\$ $line"
        eval "$line" # eval is necessary to handle pipes
    done <<EOS
./gradlew --version
./gradlew dependencyInsight --dependency com.github.shwaka.kohomology:kohomology | grep "^com.github.shwaka.kohomology" | sort | uniq
EOS
elif [ "$command" = compute ]; then
    ./gradlew run -Dtarget="$target" -Ddegree="$degree"
fi
