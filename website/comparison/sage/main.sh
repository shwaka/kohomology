#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
target=${2-}
degree=${3-}

if [ "$command" = version ]; then
    sage --version
    sage --python --version
elif [ "$command" = compute ]; then
    sage "$target".sage "$degree"
fi
