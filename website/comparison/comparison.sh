#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
comparison_dir=$(pwd)
output_file=$comparison_dir/comparison_result.json

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

result="{}"

tools="kohomology sage"
degrees="10 50 100"
for tool in $tools; do
    version_string=$("run_$tool" version)
    benchmark_result="{}"
    for degree in $degrees; do
        tempfile=$(mktemp)
        /usr/bin/time --format "%e" --output "$tempfile" "run_$tool" compute "$degree"
        benchmark_result=$(echo "$benchmark_result" |
                               jq --arg degree "$degree" \
                                  --arg time "$(cat "$tempfile")" \
                                  '.[$degree] = $time')
    done
    tool_result=$(jq --null-input \
                     --arg version_string "$version_string" \
                     --argjson benchmark_result "$benchmark_result" \
                     '{ "version": $version_string, "benchmark_result": $benchmark_result }')
    result=$(echo "$result" |
                 jq --arg tool "$tool" \
                    --argjson tool_result "$tool_result" \
                    '.[$tool] = $tool_result')
done
echo "$result" > "$output_file"
