package com.github.shwaka.kohomology.resol.monoid

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class OreFinderTest : FreeSpec({
    tags(finiteMonoidTag)

    "left Ore but not right Ore" - {
        val elements = listOf("1", "x", "y").map { SimpleFiniteMonoidElement(it) }
        val (one, x, y) = elements
        val multiplicationTable = listOf(
            listOf(one, x, y), // one*(-)
            listOf(x, x, x), // x*(-)
            listOf(y, y, y), // y*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable)

        "should be left Ore" {
            LeftOreFinder(monoid).isOre().shouldBeTrue()
        }

        "should not be right Ore" {
            RightOreFinder(monoid).isOre().shouldBeFalse()
        }
    }
})
