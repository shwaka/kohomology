#! /bin/bash

set -eu

if [ -z "${1-}" ]; then
    echo "Usage: ./release.sh 0.5" >&2
    tags=$(git tag | tac | tr "\n" " ")
    echo "Known tags: $tags" >&2
    exit 1
fi

version=$1

if ! echo "$version" | grep '^[0-9.]+$'; then
    # accepts 0.5, 1.0, 2
    # rejects 0.5-SNAPSHOT, 1.0-snapshot, v2.0
    echo "[Error] Invalid version number: $version"
    exit 1
fi

echo sed -i 's/^version = ".*"$/version = "'$version'"/' kohomology/build.gradle.kts
sed -i 's/^version = ".*"$/version = "'$version'"/' kohomology/build.gradle.kts

git commit -m "Release v$version"
git tag "v$version"

cd kohomology
./gradlew publishAllPublicationsToMyMavenRepository
