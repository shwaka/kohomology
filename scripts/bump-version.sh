#! /bin/bash

set -eu

cd $(git rev-parse --show-toplevel) # go to the root of the repository

VERSION_REGEX='^version = "\(.*\)"$'
BUILD_GRADLE_KTS=kohomology/build.gradle.kts
README_MD=README.md

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
    git add $BUILD_GRADLE_KTS
}

function update_implementation() {
    local version=$1
    local file=$2
    if [ ! -f "$file" ]; then
        echo "[ERROR] File does not exist: $file" >&2
        exit 1
    fi
    echo sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$version'")/' $file
    sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$version'")/' $file
    git add $file
}

function setup_java() {
    local sdkman_init="$HOME/.sdkman/bin/sdkman-init.sh"
    local java_version=8.0.302-open
    set +u
    source "$sdkman_init"
    sdk use java $java_version
    set -u
}

function release_version() {
    local version=$1

    update_build_gradle_kts "$version"
    update_implementation "$version" $README_MD
    update_implementation "$version" website/docs/quick-start.md

    for d in sample; do
        local kts=$d/build.gradle.kts
        update_implementation $version $kts
    done

    if ! select_yn "Do you want to git-commit? (y/n)"; then
        return
    fi
    git commit -m "Release v$version"
    git tag "v$version"

    read -p "Do you want to publish? (y/n)" answer
    if [ "$answer" = n ]; then return; fi
    cd kohomology
    setup_java
    ./gradlew publishAllPublicationsToMyMavenRepository
}

function bump_snapshot_version() {
    local snapshot_version=$1-SNAPSHOT

    update_build_gradle_kts "$snapshot_version"
    for d in profile website/kohomology-js; do
        local kts=$d/build.gradle.kts
        update_implementation $snapshot_version $kts
    done

    if ! select_yn "Do you want to git-commit? (y/n)"; then
        return
    fi
    git commit -m "Bump version to $snapshot_version"
}

function select_yn() {
    local msg=$1
    while true; do
        read -p "$msg" yn
        case $yn in
            [Yy]* ) return 0;;
            [Nn]* ) return 1;;
            * ) echo "Please answer yes/no (or y/n).";;
        esac
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
    *) echo "[Error] invalid command" >&2
       show_usage;;
esac
