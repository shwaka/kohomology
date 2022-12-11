#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
command=$1
degree=${2-10}

if [ "$command" = version ]; then
    sage --version
    sage --python --version
elif [ "$command" = compute ]; then
    sage FreeLoopSpaceOf2Sphere.sage "$degree"
fi
