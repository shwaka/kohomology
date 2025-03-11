package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.PrintType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MonomialToStringTest : FreeSpec({
    "empty monomial should be printed as 1" {
        monomialToString<StringIndeterminateName>(
            emptyList(),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "1"
    }

    "x should be printed as x" {
        monomialToString(
            listOf(Pair(StringIndeterminateName("x"), 1)),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "x"
    }

    "xy should be printed as xy" {
        monomialToString(
            listOf(
                Pair(StringIndeterminateName("x"), 1),
                Pair(StringIndeterminateName("y"), 1),
            ),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "xy"
    }
})
