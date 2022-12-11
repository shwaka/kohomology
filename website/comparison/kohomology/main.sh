#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
target=${2-}
degree=${3-}

if [ "$command" = version ]; then
    ./gradlew --version
    ./gradlew dependencyInsight --dependency com.github.shwaka.kohomology:kohomology
elif [ "$command" = compute ]; then
    ./gradlew run -Dtarget="$target" -Ddegree="$degree"
fi
