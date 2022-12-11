#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
comparison_dir=$(pwd)
output_file=$comparison_dir/../src/components/ComparisonChart/comparison.json

result="{}"

tools="kohomology sage"
degrees="[10, 50, 100]" # will be directly used in the JSON output
for tool in $tools; do
    executable=$tool/main.sh
    version_string=$("$executable" version)
    benchmark_result="[]"
    for degree in $(echo "$degrees" | jq '.[]'); do
        tempfile=$(mktemp)
        # Since bash-builtin time has no options, /usr/bin/time should be used.
        # /usr/bin/time can accept only external commands, not bash functions.
        /usr/bin/time --format "%e" --output "$tempfile" "$executable" compute "$degree"
        benchmark_result=$(echo "$benchmark_result" |
                               jq --argjson time "$(cat "$tempfile")" \
                                  '. |= . + [$time]')
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

json=$(jq --null-input \
          --argjson degrees "$degrees" \
          --argjson result "$result" \
          '{ $degrees, $result }')
echo "$json" > "$output_file"
