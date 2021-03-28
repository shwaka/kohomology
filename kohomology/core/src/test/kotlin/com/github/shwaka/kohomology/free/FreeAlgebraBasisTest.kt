package com.github.shwaka.kohomology.free

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

val freeAlgebraBasisTag = NamedTag("FreeAlgebraBasis")

class FreeAlgebraBasisTest : StringSpec({
    tags(freeAlgebraBasisTag)

    "two generators of even degrees" {
        val generators = listOf(
            FreeAlgebraGenerator("x", 2),
            FreeAlgebraGenerator("y", 2),
        )
        for ((degree, size) in listOf(Pair(0, 1), Pair(1, 0), Pair(2, 2), Pair(3, 0), Pair(4, 3))) {
            FreeAlgebraBasis.computeBasis(generators, degree).size shouldBe size
        }
    }
})
