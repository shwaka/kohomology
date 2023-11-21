package com.github.shwaka.kohomology.bar

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val finiteMonoidFromListTag = NamedTag("FiniteMonoidFromList")

class FiniteMonoidFromListTest : FreeSpec({
    tags(finiteMonoidFromListTag)

    "monoid with 3 elements containing 0" - {
        val elements = listOf("1", "t", "0").map { SimpleFiniteMonoidElement(it) }
        val (one, t, zero) = elements
        val multiplicationTable = listOf(
            listOf(one, t, zero), // one*(-)
            listOf(t, zero, zero), // t*(-)
            listOf(zero, zero, zero), // zero*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable)

        "monoid.isCommutative should be true" {
            monoid.isCommutative.shouldBeTrue()
        }

        "monoid.size should be 3" {
            monoid.size shouldBe 3
        }

        "check multiplication" {
            monoid.multiply(one, t) shouldBe t
            monoid.multiply(t, t) shouldBe zero
            monoid.multiply(zero, one) shouldBe zero
        }
    }
})
