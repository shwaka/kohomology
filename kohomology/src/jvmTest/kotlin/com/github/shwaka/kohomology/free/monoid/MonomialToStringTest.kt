package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.util.PrintType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MonomialToStringTest : FreeSpec({
    "empty monomial should be printed as 1" {
        monomialToString<IntDegree, StringIndeterminateName>(
            emptyList(),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "1"
    }

    "x should be printed as x" {
        monomialToString(
            listOf(Pair(Indeterminate("x", 2), 1)),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "x"
    }

    "xy should be printed as xy" {
        monomialToString(
            listOf(
                Pair(Indeterminate("x", 2), 1),
                Pair(Indeterminate("y", 2), 1),
            ),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "xy"
    }
})
