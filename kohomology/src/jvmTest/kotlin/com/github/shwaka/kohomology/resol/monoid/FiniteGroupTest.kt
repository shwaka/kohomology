package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

class FiniteGroupTest : FreeSpec({
    tags(finiteMonoidTag)

    "isInvertible should return BooleanWithCause.False for non-invertible case" {
        val elements = listOf("1", "x").map { SimpleFiniteMonoidElement(it) }
        val (one, x) = elements
        val multiplicationTable = listOf(
            listOf(one, x), // one*(-)
            listOf(x, x), // x*(-)
        )
        fun multiply(
            monoidElement1: SimpleFiniteMonoidElement<String>,
            monoidElement2: SimpleFiniteMonoidElement<String>
        ): SimpleFiniteMonoidElement<String> {
            val index1 = elements.indexOf(monoidElement1)
            val index2 = elements.indexOf(monoidElement2)
            return multiplicationTable[index1][index2]
        }
        fun invert(monoidElement: SimpleFiniteMonoidElement<String>): SimpleFiniteMonoidElement<String> {
            return monoidElement
        }
        val isInvertible = FiniteGroup.isInvertible(elements, ::multiply, ::invert)
        isInvertible.shouldBeInstanceOf<BooleanWithCause.False>()
        isInvertible.cause.shouldHaveSize(1)
        isInvertible.cause[0].shouldContain("x * x")
    }
})
