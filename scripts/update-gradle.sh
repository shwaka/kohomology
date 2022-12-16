#!/usr/bin/env bash
set -euo pipefail

cd "$(git rev-parse --show-toplevel)" # go to the root of the repository

version=${1-}

gradle_projects=(
    kohomology
    profile
    template
    website/kohomology-js
    website/comparison/kohomology
    website/sample
)

function show_versions() {
    echo "Current versions are:"
    for d in "${gradle_projects[@]}"; do
        f=$d/gradle/wrapper/gradle-wrapper.properties
        current_ver=$(sed -n -E 's#^.*/gradle-([0-9.]+)-bin\.zip#\1#p' < "$f")
        echo "$current_ver in $d"
    done
}

function update() {
    for d in "${gradle_projects[@]}"; do
        f=$d/gradle/wrapper/gradle-wrapper.properties
        sed -i -E "s#(distributionUrl=.*/gradle-)[0-9.]+(-bin.zip)#\\1$version\\2#" "$f"
    done
}

if [ -z "$version" ]; then
    echo "[Error] Input gradle version" >&2
    echo "Usage: ./update-gradle.sh 99.9.9" >&2
    show_versions >&2
    exit 1
else
    update
fi
