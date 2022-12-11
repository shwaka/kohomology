#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
target=${2-}
degree=${3-}

if [ "$command" = version ]; then
    while read line; do
        echo "\$ $line"
        eval "$line"
    done <<EOS
sage --version
sage --python --version
EOS
elif [ "$command" = compute ]; then
    sage "$target".sage "$degree"
fi
