package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val finiteMonoidFromListTag = NamedTag("FiniteMonoidFromList")

class FiniteMonoidFromListTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidFromListTag)

    "monoid with 3 elements containing 0" - {
        val elements = listOf("1", "t", "0").map { SimpleFiniteMonoidElement(it) }
        val (one, t, zero) = elements
        val multiplicationTable = listOf(
            listOf(one, t, zero), // one*(-)
            listOf(t, zero, zero), // t*(-)
            listOf(zero, zero, zero), // zero*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable, name = "M", texName = "M'")

        "monoid.checkMonoidAxioms() should not throw" {
            shouldNotThrow<IllegalStateException> {
                monoid.checkMonoidAxioms()
            }
        }

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

        "test pow" {
            monoid.context.run {
                one.pow(0) shouldBe one
                t.pow(0) shouldBe one
                zero.pow(0) shouldBe one
                t.pow(1) shouldBe t
                t.pow(2) shouldBe zero
                t.pow(500) shouldBe zero
            }
        }

        "pow(-1) should throw IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                monoid.context.run {
                    one.pow(-1)
                }
            }
        }

        "test context" {
            monoid.context.run {
                (one * t) shouldBe t
                (t * t) shouldBe zero
                (zero * one) shouldBe zero
            }
        }

        "test toString" {
            monoid.toString() shouldBe "M"
            monoid.toString(PrintConfig(PrintType.PLAIN)) shouldBe "M"
            monoid.toString(PrintConfig(PrintType.TEX)) shouldBe "M'"
            monoid.toString(PrintConfig(PrintType.CODE)) shouldBe "M"
        }
    }

    "non-commutative monoid with 3 elements" - {
        val elements = listOf("1", "x", "y").map { SimpleFiniteMonoidElement(it) }
        val (one, x, y) = elements
        val multiplicationTable = listOf(
            listOf(one, x, y), // one*(-)
            listOf(x, x, y), // x*(-)
            listOf(y, x, y), // y*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable, "M")

        "monoid.checkMonoidAxioms() should not throw" {
            shouldNotThrow<IllegalStateException> {
                monoid.checkMonoidAxioms()
            }
        }

        "monoid.isCommutative should be false" {
            monoid.isCommutative.shouldBeFalse()
        }

        "monoid.size should be 3" {
            monoid.size shouldBe 3
        }

        "check multiplication" {
            monoid.multiply(one, x) shouldBe x
            monoid.multiply(x, x) shouldBe x
            monoid.multiply(x, y) shouldBe y
            monoid.multiply(y, x) shouldBe x
        }
    }
})
