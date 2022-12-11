#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
comparison_dir=$(pwd)

function run_kohomology() {
    local command=$1
    local degree=${2-10}
    cd "$comparison_dir/kohomology"
    if [ "$command" = version ]; then
        ./gradlew --version
        ./gradlew dependencyInsight --dependency com.github.shwaka.kohomology:kohomology
    elif [ "$command" = compute ]; then
        ./gradlew run -Dname=FreeLoopSpaceOf2Sphere -Ddegree="$degree"
    fi
}

function run_sage() {
    local command=$1
    local degree=${2-10}
    cd "$comparison_dir/sage"
    if [ "$command" = version ]; then
        sage --version
        sage --python --version
    elif [ "$command" = compute ]; then
        sage FreeLoopSpaceOf2Sphere.sage "$degree"
    fi
}

tools="kohomology sage"
degrees="10 50 100"
for tool in $tools; do
    "run_$tool" version
    for degree in $degrees; do
        "run_$tool" compute "$degree"
    done
done
