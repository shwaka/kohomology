#! /bin/bash

set -eu

if [ -z "${1-}" ]; then
    echo "Usage: ./bump-version.sh 0.5-SNAPSHOT" >&2
    exit 1
fi

version=$1

echo sed -i 's/^version = ".*"$/version = "'$version'"/' kohomology/build.gradle.kts
sed -i 's/^version = ".*"$/version = "'$version'"/' kohomology/build.gradle.kts
for d in benchmark profile; do
    kts=$d/build.gradle.kts
    echo sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$version'")/' $kts
    sed -i 's/implementation("com.github.shwaka.kohomology:kohomology:.*")$/implementation("com.github.shwaka.kohomology:kohomology:'$version'")/' $kts
done
