#!/usr/bin/env bash
set -euo pipefail

cd ../kohomology/
./gradlew writeDepGraph

cd ../website/static/img/uml
plantuml depGraph.uml
