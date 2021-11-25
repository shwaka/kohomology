[![version](https://img.shields.io/badge/dynamic/xml?label=version&query=%2F%2Fmetadata%2Fversioning%2Flatest&url=https%3A%2F%2Fshwaka.github.io%2Fmaven%2Fcom%2Fgithub%2Fshwaka%2Fkohomology%2Fkohomology%2Fmaven-metadata.xml)](https://shwaka.github.io/maven/com/github/shwaka/kohomology/)
[![build](https://github.com/shwaka/kohomology/actions/workflows/gradle.yml/badge.svg)](https://github.com/shwaka/kohomology/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/shwaka/kohomology/branch/main/graph/badge.svg?token=kTXiaOtBj1)](https://codecov.io/gh/shwaka/kohomology)
[![license](https://img.shields.io/github/license/shwaka/kohomology)](https://github.com/shwaka/kohomology/blob/main/LICENSE)
[![KDoc](https://img.shields.io/badge/dynamic/json?color=lightgray&label=KDoc&query=percent&suffix=%25&url=https%3A%2F%2Fshwaka.github.io%2Fkohomology%2Fdokka%2Fcoverage.json&logo=kotlin&logoColor=orange)](https://shwaka.github.io/kohomology/dokka/index.html)

`kohomology` is a [Kotlin](https://kotlinlang.org/) library to compute the cohomology of a cochain complex. The main target is a Sullivan algebra (in [Rational homotopy theory - Wikipedia](https://en.wikipedia.org/wiki/Rational_homotopy_theory)).

- This library is a [kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) project. So it can be compiled for JVM and browser. (See [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app))
- This library also supports fields of positive characteristic. (But fewer implementation of concrete cochain complexes)
- The name "*ko*homology" is obtained by combining "cohomology" and "kotlin".

## Usage
This library is published at the maven repository [shwaka/maven](https://github.com/shwaka/maven).
With gradle, you can use it by:
```kotlin
repositories {
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    implementation("com.github.shwaka.kohomology:kohomology:0.2")
    // version might be old, see the badge at the top of this README
}
```

Here is an example script computing the cohomology of the free loop space of the even dimensional sphere.
```kotlin
val sphereDim = 4
val indeterminateList = listOf(
    Indeterminate("x", sphereDim),
    Indeterminate("y", sphereDim * 2 - 1)
)
val matrixSpace = SparseMatrixSpaceOverBigRational
val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
    listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2
}

for (degree in 0 until 10) {
    val basis = sphere.cohomology[degree].getBasis()
    println("H^$degree(S^$sphereDim) = Q$basis")
}

val freeLoopSpace = FreeLoopSpace(sphere)
val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

freeLoopSpace.context.run {
    // Operations in a DGA can be applied within 'context.run'
    println("dsy = ${d(sy)} = ${-2 * x * sx}")
}

for (degree in 0 until 25) {
    val basis = freeLoopSpace.cohomology[degree].getBasis()
    println("H^$degree(LS^$sphereDim) = Q$basis")
}
```

See tests in [kohomology/src/jvmTest/kotlin/com/github/shwaka/kohomology](kohomology/src/jvmTest/kotlin/com/github/shwaka/kohomology) for more examples.
You can find complete examples in [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app).
Auto-generated documentation can be seen [here](https://shwaka.github.io/kohomology/dokka/index.html) (but currently very few descriptions are given).

## Overview of classes and interfaces
![classes](uml/packages.png)
