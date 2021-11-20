#! /bin/bash

set -eu

VERSION_REGEX='^version = "\(.*\)"$'
BUILD_GRADLE_KTS=kohomology/build.gradle.kts

function show_usage() {
    echo "Usage:" >&2
    echo "  ./bump-version.sh release 99.9" >&2
    echo "  ./bump-version.sh snapshot 99.9" >&2
    current_version=$(cat $BUILD_GRADLE_KTS |
                          grep "$VERSION_REGEX" |
                          sed s/"$VERSION_REGEX"/\\1/)
    echo "Current version: $current_version" >&2
    tags=$(git tag | tac | tr "\n" " ")
    echo "Known tags: $tags" >&2
}

function update_build_gradle_kts() {
    version=$1

    echo sed -i s/"$VERSION_REGEX"/"version = \"$version\""/ $BUILD_GRADLE_KTS
    sed -i s/"$VERSION_REGEX"/"version = \"$version\""/ $BUILD_GRADLE_KTS
}

function release_version() {
    local version=$1

    update_build_gradle_kts "$version"

    git commit -m "Release v$version"
    git tag "v$version"

    cd kohomology
    ./gradlew publishAllPublicationsToMyMavenRepository
}

function bump_snapshot_version() {
    local snapshot_version=$1-SNAPSHOT

    update_build_gradle_kts "$snapshot_version"
    for d in benchmark profile; do
        local kts=$d/build.gradle.kts
        echo sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$snapshot_version'")/' $kts
        sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$snapshot_version'")/' $kts
    done
}

if [ -z "${1-}" -o -z "${2-}" ]; then
    show_usage
    exit 1
fi

command=$1
version=$2

if ! echo "$version" | grep -E '^[0-9.]+$'; then
    # accepts 0.5, 1.0, 2
    # rejects 0.5-SNAPSHOT, 1.0-snapshot, v2.0
    echo "[Error] Invalid version number: $version" >&2
    show_usage
    exit 1
fi

case "$command" in
    release) release_version "$version";;
    snapshot) bump_snapshot_version "$version";;
    *) echo "[Error] invalid command"
       show_usage
esac
