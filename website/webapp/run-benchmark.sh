#!/usr/bin/env bash
set -eu

output_dir=benchmark-result
mkdir -p $output_dir

jest_report_file=$output_dir/jestReport.json
output_file=$output_dir/output.json

npm test benchmark.test.tsx -- --verbose --json --outputFile=$jest_report_file

jq_filter=$(cat <<EOS
.testResults[].assertionResults[] |
  {
    name: .fullName,
    value: (.duration / 1000),
    unit: "s/op"
  }
EOS
         )
jq "$jq_filter" $jest_report_file | jq -s '.' > $output_file
