#!/usr/bin/env bash
set -euo pipefail

cd "$(git rev-parse --show-toplevel)" # go to the root of the repository

version=${1-}

config_files=$(find . -name build.gradle.kts | grep -v buildSrc)

function show_version_in_file() {
    local file=$1
    from_literal=$(sed -n -E 's#^ *kotlin\("(jvm|js|multiplatform)"\) version "([0-9.]+)"#\2#p' < "$f")
    if [ -n "$from_literal" ]; then
        echo "$from_literal"
        return 0
    fi
    from_kotlinVersion=$(sed -n -E 's#^ *val kotlinVersion = "([0-9.]+)"#\1#p' < "$file")
    if [ -n "$from_kotlinVersion" ]; then
        echo "$from_kotlinVersion"
        return 0
    fi
    echo "Version not found in $file" >&2
    return 1
}

function show_versions() {
    echo "Current versions are:"
    for f in $config_files; do
        current_ver=$(show_version_in_file "$f")
        echo "$current_ver in $f"
    done
}

function update_version_in_file() {
    local file=$1
    local version=$2
    sed -i -E "s#^( *kotlin\\(\"(jvm|js|multiplatform)\"\\) version \")[0-9.]+(\")#\\1$version\\3#" "$file"
    sed -i -E "s#^( *val kotlinVersion = \")[0-9.]+(\")#\\1$version\\2#" "$file"
}

function update_versions() {
    local version=$1
    for f in $config_files; do
        update_version_in_file "$f" "$version"
    done
}

if [ -z "$version" ]; then
    show_versions
    echo "Usage: ./update-kotlin.sh 99.9.9"
    exit 0
else
    update_versions "$version"
fi
