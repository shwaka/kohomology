#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
degree=${2-10}

if [ "$command" = version ]; then
    ./gradlew --version
    ./gradlew dependencyInsight --dependency com.github.shwaka.kohomology:kohomology
elif [ "$command" = compute ]; then
    ./gradlew run -Dname=FreeLoopSpaceOf2Sphere -Ddegree="$degree"
fi
