[![version](https://img.shields.io/badge/dynamic/xml?label=version&query=%2F%2Fmetadata%2Fversioning%2Flatest&url=https%3A%2F%2Fshwaka.github.io%2Fmaven%2Fcom%2Fgithub%2Fshwaka%2Fkohomology%2Fkohomology%2Fmaven-metadata.xml)](https://shwaka.github.io/maven/com/github/shwaka/kohomology/)
[![build](https://github.com/shwaka/kohomology/actions/workflows/main.yml/badge.svg)](https://github.com/shwaka/kohomology/actions/workflows/main.yml)
[![codecov](https://codecov.io/gh/shwaka/kohomology/branch/main/graph/badge.svg?token=kTXiaOtBj1)](https://codecov.io/gh/shwaka/kohomology)
[![license](https://img.shields.io/github/license/shwaka/kohomology)](https://github.com/shwaka/kohomology/blob/main/LICENSE)
[![KDoc](https://img.shields.io/badge/dynamic/json?color=lightgray&label=KDoc&query=percent&suffix=%25&url=https%3A%2F%2Fshwaka.github.io%2Fkohomology%2Fdokka%2Fcoverage.json&logo=kotlin&logoColor=orange)](https://shwaka.github.io/kohomology/dokka/index.html)

`kohomology` is a [Kotlin](https://kotlinlang.org/) library to compute the cohomology of a cochain complex. The main target is a Sullivan algebra (in [Rational homotopy theory - Wikipedia](https://en.wikipedia.org/wiki/Rational_homotopy_theory)).

- This library is a [kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) project. So it can be compiled for JVM and browser. (See [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app))
- This library also supports fields of positive characteristic. (But fewer implementation of concrete cochain complexes)
- The name "*ko*homology" is obtained by combining "cohomology" and "kotlin".

## Documentation
See [Kohomology](https://shwaka.github.io/kohomology/index.html) for tutorial and documentation. (currently under development)
