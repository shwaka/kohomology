#!/usr/bin/env bash
set -euo pipefail

cd "$(git rev-parse --show-toplevel)" # go to the root of the repository

version=${1-}

properties_files=$(find . -name gradle-wrapper.properties)

function show_versions() {
    echo "Current versions are:"
    for f in $properties_files; do
        current_ver=$(sed -n -E 's#^.*/gradle-([0-9.]+)-bin\.zip#\1#p' < "$f")
        echo "$current_ver in $f"
    done
}

function update() {
    for f in $properties_files; do
        sed -i -E "s#(distributionUrl=.*/gradle-)[0-9.]+(-bin\\.zip)#\\1$version\\2#" "$f"
    done
}

if [ -z "$version" ]; then
    show_versions
    echo "Usage: ./update-gradle.sh 99.9.9"
    exit 0
else
    update
fi
