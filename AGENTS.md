# AGENTS.md

This file applies to the whole repository.

## Placement Policy

Keep a single `AGENTS.md` at the repository root for now. The repository has several
independent Gradle/Node work areas, but the main agent guidance is shared: choose the
right working directory, run the matching checks, and avoid generated outputs.

Add a nested `AGENTS.md` only when a subdirectory gains rules that would be noisy or
misleading globally, for example a substantially different workflow under
`website/webapp/` or `template/`.

## Repository Layout

- `kohomology/`: main Kotlin Multiplatform library.
- `website/webapp/`: Docusaurus site and React/TypeScript calculator UI.
- `website/kohomology-js/`: Kotlin/JS wrapper used by the website.
- `template/`: template project for users of the library.
- `profile/`: profiling/benchmark-related Gradle project.
- `scripts/`: repository maintenance and site preparation scripts.
- `benchmark-data/`: benchmark data used by the website.

Avoid editing generated or dependency directories such as `build/`,
`website/webapp/node_modules/`, `website/webapp/.docusaurus/`, and Gradle cache
directories unless the task explicitly requires it.

## Core Library Workflow

Run Gradle commands from `kohomology/`.

- Compile JVM code: `./gradlew compileKotlinJvm`
- Run JVM tests: `./gradlew jvmTest`
- Run a tagged Kotest subset: `./gradlew jvmTest -Dkotest.tags=<TagName>`
- Run detekt: `./gradlew detekt`
- Auto-correct detekt formatting where supported: `./gradlew lf`
- Build local publication without native artifacts: `./gradlew publishToMavenLocal -Dkohomology.disableNative`

The project uses JDK 17. Most production code belongs in
`kohomology/src/commonMain/kotlin` unless it is truly platform-specific. JVM tests live
under `kohomology/src/jvmTest/kotlin`.

When changing public APIs, remember that `explicitApiWarning()` is enabled. Add or
adjust visibility, return types, and KDoc deliberately rather than relying on inference.

## Website Workflow

Run Node commands from `website/webapp/`. Volta pins the expected Node and npm versions
in `package.json`.

- Install dependencies: `npm ci`
- Start local docs site: `npm run start`
- Run component/unit tests: `npm test -- --verbose`
- Typecheck: `npm run tsc`
- Lint: `npm run eslint`
- Build: `npm run build`

The website depends on the local Kotlin/JS package. If that package is stale or missing,
prepare it from the repository root with `./scripts/prepare-site.sh kohomology-js`.
Some typecheck/build paths also require benchmark data; CI uses
`./scripts/prepare-site.sh benchmark-data-mock` for typechecking.

## Change Guidelines

- Prefer existing package structure, naming conventions, and test patterns over new
  abstractions.
- Keep library changes multiplatform-compatible unless the target source set is
  platform-specific.
- Add or update focused tests for behavior changes. Use Kotest tags when a smaller JVM
  test subset is enough during iteration, then run the broader relevant check before
  finishing.
- Do not rewrite generated files, lockfiles, or vendored dependency trees as incidental
  cleanup.
- Keep documentation changes close to the code or workflow they describe.

## CI Reference

The main GitHub Actions workflow uses JDK 17 and runs core build/test/lint, Kotlin/JS
tests, website tests/typecheck/lint, and the website build. Useful local equivalents are:

- Core: `cd kohomology && ./gradlew compileKotlinJvm jvmTest detekt`
- Website: prepare `kohomology-js` if needed, then
  `cd website/webapp && npm test -- --verbose && npm run tsc && npm run eslint`
