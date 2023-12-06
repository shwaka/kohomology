package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

class FiniteMonoidTest : FreeSpec({
    tags(finiteMonoidTag)

    "isUnital should return BooleanWithCause.False for non-unital case" {
        val elements = listOf("1", "x").map { SimpleFiniteMonoidElement(it) }
        val (one, x) = elements
        val multiplicationTable = listOf(
            listOf(one, one), // one*(-)
            listOf(one, x), // x*(-)
        )
        fun multiply(
            monoidElement1: SimpleFiniteMonoidElement<String>,
            monoidElement2: SimpleFiniteMonoidElement<String>
        ): SimpleFiniteMonoidElement<String> {
            val index1 = elements.indexOf(monoidElement1)
            val index2 = elements.indexOf(monoidElement2)
            return multiplicationTable[index1][index2]
        }
        val isUnital = FiniteMonoid.isUnital(elements, ::multiply)
        isUnital.shouldBeInstanceOf<BooleanWithCause.False>()
        isUnital.cause.shouldHaveSize(2)
        isUnital.cause[0].shouldContain("x * 1")
        isUnital.cause[1].shouldContain("1 * x")
    }

    "isAssociative should return BooleanWithCause.False for non-associative case" {
        val elements = listOf("1", "x", "y").map { SimpleFiniteMonoidElement(it) }
        val (one, x, y) = elements
        val multiplicationTable = listOf(
            listOf(one, x, y), // one*(-)
            listOf(x, y, y), // x*(-)
            listOf(y, x, y), // y*(-)
        )
        fun multiply(
            monoidElement1: SimpleFiniteMonoidElement<String>,
            monoidElement2: SimpleFiniteMonoidElement<String>
        ): SimpleFiniteMonoidElement<String> {
            val index1 = elements.indexOf(monoidElement1)
            val index2 = elements.indexOf(monoidElement2)
            return multiplicationTable[index1][index2]
        }
        val isAssociative = FiniteMonoid.isAssociative(elements, ::multiply)
        isAssociative.shouldBeInstanceOf<BooleanWithCause.False>()
        isAssociative.cause.shouldHaveSize(2)
        isAssociative.cause[0].shouldContain("(x * x) * x")
        isAssociative.cause[1].shouldContain("(x * y) * x")
    }
})
