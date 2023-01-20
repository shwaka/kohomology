#!/usr/bin/env bash
set -euo pipefail

gitRoot=$(git rev-parse --show-toplevel) # root of the repository

umlPath=$gitRoot/website/static/img/uml/depGraph.uml

cd ../kohomology/
./gradlew writeDepGraph -DumlPath="$umlPath"

# cd ../website/static/img/uml
plantuml "$umlPath"
