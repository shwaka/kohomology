package com.github.shwaka.kohomology.free

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val freeAlgebraBasisTag = NamedTag("FreeAlgebraBasis")

class FreeAlgebraBasisTest : StringSpec({
    tags(freeAlgebraBasisTag)

    "two generators of even degrees" {
        val generators = listOf(
            FreeAlgebraGenerator("x", 2),
            FreeAlgebraGenerator("y", 2),
        )
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 0), Pair(2, 2), Pair(3, 0), Pair(4, 3)))
        checkAll(gen) { (degree, size) ->
            FreeAlgebraBasis.computeBasis(generators, degree).size shouldBe size
        }
    }

    "polynomial algebra tensor exterior algebra" {
        val generators = listOf(
            FreeAlgebraGenerator("x", 1),
            FreeAlgebraGenerator("y", 2),
        )
        val gen = exhaustive(listOf(0, 1, 2, 3, 4))
        checkAll(gen) { degree ->
            FreeAlgebraBasis.computeBasis(generators, degree).size shouldBe 1
        }
    }
})
