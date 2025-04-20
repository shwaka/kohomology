#!/usr/bin/env bash
set -eu

for cmd in node npm; do
    echo $cmd
    echo "  path: $(command -v $cmd)"
    echo "  version: $($cmd --version)"
    command -v $cmd | grep volta > /dev/null
done
