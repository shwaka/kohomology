#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
comparison_dir=$(pwd)
output_file=$comparison_dir/../src/components/ComparisonChart/comparison.json

tools="kohomology sage"
targets=$(cat <<EOS
{
  "FreeLoopSpaceOf2Sphere": {
    "degrees": [0, 10, 50, 100]
  }
}
EOS
)

result="{}"
for tool in $tools; do
    executable=$tool/main.sh
    version_string=$("$executable" version)
    result_tool_bench="{}"
    for target in $(echo "$targets" | jq --raw-output 'keys | .[]'); do
        result_target="[]"
        degrees=$(echo "$targets" | jq --arg target "$target" '.[$target].degrees | .[]')
        for degree in $degrees; do
            tempfile=$(mktemp)
            # Since bash-builtin time has no options, /usr/bin/time should be used.
            # /usr/bin/time can accept only external commands, not bash functions.
            /usr/bin/time --format "%e" --output "$tempfile" \
                          "$executable" compute "$target" "$degree"
            result_target=$(echo "$result_target" |
                                   jq --argjson time "$(cat "$tempfile")" \
                                      '. |= . + [$time]')
        done
        result_tool_bench=$(echo "$result_tool_bench" |
                          jq --arg target "$target" \
                             --argjson result_target "$result_target" \
                             '. |= . + { ($target): $result_target }')
    done
    result_tool=$(jq --null-input \
                     --arg version_string "$version_string" \
                     --argjson result_tool_bench "$result_tool_bench" \
                     '{ "version": $version_string, "benchmark": $result_tool_bench }')
    result=$(echo "$result" |
                 jq --arg tool "$tool" \
                    --argjson result_tool "$result_tool" \
                    '.[$tool] = $result_tool')
done

json=$(jq --null-input \
          --argjson targets "$targets" \
          --argjson result "$result" \
          '{ $targets, $result }')
echo "$json" > "$output_file"
